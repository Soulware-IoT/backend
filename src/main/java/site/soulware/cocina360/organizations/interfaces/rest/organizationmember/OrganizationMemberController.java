package site.soulware.cocina360.organizations.interfaces.rest.organizationmember;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.soulware.cocina360.organizations.application.organizationmember.OrganizationMemberCommandService;
import site.soulware.cocina360.organizations.application.organizationmember.OrganizationMemberQueryService;
import site.soulware.cocina360.organizations.application.organization.OrganizationQueryService;
import site.soulware.cocina360.organizations.domain.model.command.RemoveOrganizationMemberCommand;
import site.soulware.cocina360.organizations.domain.model.query.GetOrganizationMemberQuery;
import site.soulware.cocina360.organizations.domain.model.query.GetOrganizationMembershipQuery;
import site.soulware.cocina360.organizations.domain.model.query.GetOrganizationQuery;
import site.soulware.cocina360.organizations.domain.model.query.ListOrganizationMembersQuery;
import site.soulware.cocina360.organizations.interfaces.acl.AccessLevel;
import site.soulware.cocina360.organizations.interfaces.acl.AuthorizationApi;
import site.soulware.cocina360.organizations.interfaces.acl.PermissionArea;
import site.soulware.cocina360.organizations.interfaces.rest.organizationmember.request.UpdateMemberPermissionsRequest;
import site.soulware.cocina360.organizations.interfaces.rest.organizationmember.response.OrganizationMemberResponse;
import site.soulware.cocina360.shared.infrastructure.auth.CurrentUser;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/organizations/{organizationId}/members")
public class OrganizationMemberController {

    private final OrganizationMemberCommandService commandService;
    private final OrganizationMemberQueryService queryService;
    private final OrganizationQueryService organizationQueryService;
    private final AuthorizationApi authorizationApi;

    public OrganizationMemberController(
        OrganizationMemberCommandService commandService,
        OrganizationMemberQueryService queryService,
        OrganizationQueryService organizationQueryService,
        AuthorizationApi authorizationApi
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.organizationQueryService = organizationQueryService;
        this.authorizationApi = authorizationApi;
    }

    @GetMapping
    public ResponseEntity<List<OrganizationMemberResponse>> list(
        @PathVariable UUID organizationId,
        @CurrentUser UUID requesterId
    ) {
        this.requireOrganization(organizationId);
        this.authorizationApi.requirePermission(organizationId, requesterId, PermissionArea.ORGANIZATIONS, AccessLevel.ASSIGNEE);
        return ResponseEntity.ok(
                this.queryService.handle(new ListOrganizationMembersQuery(organizationId))
                        .stream().map(OrganizationMemberResponse::from).toList());
    }

    @GetMapping("/me")
    public ResponseEntity<OrganizationMemberResponse> getMine(
        @PathVariable UUID organizationId,
        @CurrentUser UUID requesterId
    ) {
        this.requireOrganization(organizationId);
        return ResponseEntity.ok(
                OrganizationMemberResponse.from(
                        this.queryService.handle(new GetOrganizationMembershipQuery(organizationId, requesterId))));
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<OrganizationMemberResponse> getById(
        @PathVariable UUID organizationId,
        @PathVariable UUID memberId,
        @CurrentUser UUID requesterId
    ) {
        this.requireOrganization(organizationId);
        this.authorizationApi.requirePermission(organizationId, requesterId, PermissionArea.ORGANIZATIONS, AccessLevel.ASSIGNEE);
        return ResponseEntity.ok(
                OrganizationMemberResponse.from(
                        this.queryService.handle(new GetOrganizationMemberQuery(organizationId, memberId))));
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> remove(
        @PathVariable UUID organizationId,
        @PathVariable UUID memberId,
        @CurrentUser UUID requesterId
    ) {
        this.requireOrganization(organizationId);
        this.authorizationApi.requirePermission(organizationId, requesterId, PermissionArea.ORGANIZATIONS, AccessLevel.ADMIN);
        this.commandService.handle(new RemoveOrganizationMemberCommand(organizationId, memberId));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{memberId}/permissions")
    public ResponseEntity<OrganizationMemberResponse> updatePermissions(
        @PathVariable UUID organizationId,
        @PathVariable UUID memberId,
        @RequestBody @Valid UpdateMemberPermissionsRequest request,
        @CurrentUser UUID requesterId
    ) {
        this.requireOrganization(organizationId);
        // Floor: must be at least LIEUTENANT to touch permissions; the strict "assign below own"
        // rule is enforced in the command service using the actor's own level.
        this.authorizationApi.requirePermission(organizationId, requesterId, PermissionArea.ORGANIZATIONS, AccessLevel.LIEUTENANT);
        this.commandService.handle(request.toCommand(organizationId, memberId, requesterId));

        return ResponseEntity.ok(
                OrganizationMemberResponse.from(
                        this.queryService.handle(new GetOrganizationMemberQuery(organizationId, memberId))));
    }

    private void requireOrganization(UUID organizationId) {
        this.organizationQueryService.handle(new GetOrganizationQuery(organizationId));
    }
}
