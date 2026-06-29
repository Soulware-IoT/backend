package site.soulware.cocina360.organizations.interfaces.rest.invitation;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.soulware.cocina360.organizations.application.invitation.InvitationCommandService;
import site.soulware.cocina360.organizations.application.invitation.InvitationQueryService;
import site.soulware.cocina360.organizations.application.invitation.InvitationResult;
import site.soulware.cocina360.organizations.application.organization.OrganizationQueryService;
import site.soulware.cocina360.organizations.domain.model.command.AcceptInvitationCommand;
import site.soulware.cocina360.organizations.domain.model.command.DeclineInvitationCommand;
import site.soulware.cocina360.organizations.domain.model.query.GetInvitationQuery;
import site.soulware.cocina360.organizations.domain.model.query.GetOrganizationQuery;
import site.soulware.cocina360.organizations.domain.model.query.ListInvitationsByInvitedEmailQuery;
import site.soulware.cocina360.organizations.domain.model.query.ListOrganizationInvitationsQuery;
import site.soulware.cocina360.organizations.interfaces.acl.AccessLevel;
import site.soulware.cocina360.organizations.interfaces.acl.AuthorizationApi;
import site.soulware.cocina360.organizations.interfaces.acl.PermissionArea;
import site.soulware.cocina360.organizations.interfaces.rest.invitation.request.InviteRequest;
import site.soulware.cocina360.organizations.interfaces.rest.invitation.response.InvitationResponse;
import site.soulware.cocina360.profiles.interfaces.acl.ProfilesApi;
import site.soulware.cocina360.shared.infrastructure.auth.CurrentUser;

import java.util.List;
import java.util.UUID;

@RestController
public class InvitationController {

    private final InvitationCommandService commandService;
    private final InvitationQueryService queryService;
    private final OrganizationQueryService organizationQueryService;
    private final ProfilesApi profilesApi;
    private final AuthorizationApi authorizationApi;

    public InvitationController(
        InvitationCommandService commandService,
        InvitationQueryService queryService,
        OrganizationQueryService organizationQueryService,
        ProfilesApi profilesApi,
        AuthorizationApi authorizationApi
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.organizationQueryService = organizationQueryService;
        this.profilesApi = profilesApi;
        this.authorizationApi = authorizationApi;
    }

    @PostMapping("/organizations/{organizationId}/invitations")
    public ResponseEntity<Void> invite(
        @PathVariable UUID organizationId,
        @RequestBody @Valid InviteRequest request,
        @CurrentUser UUID requesterId
    ) {
        this.requireOrganization(organizationId);
        this.authorizationApi.requirePermission(organizationId, requesterId, PermissionArea.ORGANIZATIONS, AccessLevel.ADMIN);
        this.commandService.handle(request.toCommand(organizationId, requesterId));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/organizations/{organizationId}/invitations")
    public ResponseEntity<List<InvitationResponse>> listByOrganization(
        @PathVariable UUID organizationId,
        @CurrentUser UUID requesterId
    ) {
        this.requireOrganization(organizationId);
        this.authorizationApi.requirePermission(organizationId, requesterId, PermissionArea.ORGANIZATIONS, AccessLevel.ASSIGNEE);
        return ResponseEntity.ok(
                this.queryService.handle(new ListOrganizationInvitationsQuery(organizationId))
                        .stream().map(InvitationResponse::from).toList());
    }

    @GetMapping("/invitations")
    public ResponseEntity<List<InvitationResponse>> listMine(@CurrentUser UUID requesterId) {
        String invitedEmail = this.profilesApi.requireEmailByProfileId(requesterId);
        return ResponseEntity.ok(
                this.queryService.handle(new ListInvitationsByInvitedEmailQuery(invitedEmail))
                        .stream().map(InvitationResponse::from).toList());
    }

    @GetMapping("/invitations/{id}")
    public ResponseEntity<InvitationResponse> getById(
        @PathVariable UUID id,
        @CurrentUser UUID requesterId
    ) {
        // Fetch (404 if missing) to learn the owning org, then authorize before returning.
        InvitationResult invitation = this.queryService.handle(new GetInvitationQuery(id));
        this.authorizationApi.requirePermission(invitation.organizationId(), requesterId, PermissionArea.ORGANIZATIONS, AccessLevel.ASSIGNEE);
        return ResponseEntity.ok(InvitationResponse.from(invitation));
    }

    @PostMapping("/invitations/{id}/accept")
    public ResponseEntity<InvitationResponse> accept(
        @PathVariable UUID id,
        @CurrentUser UUID requesterId
    ) {
        this.profilesApi.requireProfileId(requesterId);
        this.commandService.handle(new AcceptInvitationCommand(id, requesterId));

        return ResponseEntity.ok(
                InvitationResponse.from(this.queryService.handle(new GetInvitationQuery(id))));
    }

    @PostMapping("/invitations/{id}/decline")
    public ResponseEntity<InvitationResponse> decline(
        @PathVariable UUID id,
        @CurrentUser UUID requesterId
    ) {
        this.profilesApi.requireProfileId(requesterId);
        this.commandService.handle(new DeclineInvitationCommand(id, requesterId));

        return ResponseEntity.ok(
                InvitationResponse.from(this.queryService.handle(new GetInvitationQuery(id))));
    }

    private void requireOrganization(UUID organizationId) {
        this.organizationQueryService.handle(new GetOrganizationQuery(organizationId));
    }
}
