package site.soulware.cocina360.organizations.interfaces.rest.organization;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.soulware.cocina360.organizations.application.organization.OrganizationCommandService;
import site.soulware.cocina360.organizations.application.organization.OrganizationQueryService;
import site.soulware.cocina360.organizations.domain.model.command.DeleteOrganizationCommand;
import site.soulware.cocina360.organizations.domain.model.query.GetOrganizationQuery;
import site.soulware.cocina360.organizations.domain.model.query.ListOrganizationsByProfileQuery;
import site.soulware.cocina360.organizations.interfaces.acl.AccessLevel;
import site.soulware.cocina360.organizations.interfaces.acl.AuthorizationApi;
import site.soulware.cocina360.organizations.interfaces.acl.PermissionArea;
import site.soulware.cocina360.organizations.interfaces.rest.organization.request.CreateOrganizationRequest;
import site.soulware.cocina360.organizations.interfaces.rest.organization.request.UpdateOrganizationRequest;
import site.soulware.cocina360.organizations.interfaces.rest.organization.response.OrganizationResponse;
import site.soulware.cocina360.profiles.interfaces.acl.ProfilesApi;
import site.soulware.cocina360.shared.domain.model.exception.SelfAccessRequiredException;
import site.soulware.cocina360.shared.infrastructure.auth.CurrentUser;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/organizations")
public class OrganizationController {

    private final OrganizationCommandService commandService;
    private final OrganizationQueryService queryService;
    private final ProfilesApi profilesApi;
    private final AuthorizationApi authorizationApi;

    public OrganizationController(
        OrganizationCommandService commandService,
        OrganizationQueryService queryService,
        ProfilesApi profilesApi,
        AuthorizationApi authorizationApi
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.profilesApi = profilesApi;
        this.authorizationApi = authorizationApi;
    }

    @PostMapping
    public ResponseEntity<OrganizationResponse> create(
        @RequestBody @Valid CreateOrganizationRequest request,
        @CurrentUser UUID requesterId
    ) {
        this.profilesApi.requireProfileId(requesterId);
        var organizationId = this.commandService.handle(request.toCommand(requesterId));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(OrganizationResponse.from(this.queryService.handle(new GetOrganizationQuery(organizationId.value()))));
    }

    @GetMapping(params = "profileId")
    public ResponseEntity<List<OrganizationResponse>> listByProfile(
        @RequestParam UUID profileId,
        @CurrentUser UUID requesterId
    ) {
        this.requireSelf(requesterId, profileId);
        List<OrganizationResponse> organizations = this.queryService
                .handle(new ListOrganizationsByProfileQuery(profileId)).stream()
                .map(OrganizationResponse::from)
                .toList();
        return ResponseEntity.ok(organizations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationResponse> getById(
        @PathVariable UUID id,
        @CurrentUser UUID requesterId
    ) {
        this.authorizationApi.requirePermission(id, requesterId, PermissionArea.ORGANIZATIONS, AccessLevel.ASSIGNEE);
        return ResponseEntity.ok(
                OrganizationResponse.from(this.queryService.handle(new GetOrganizationQuery(id))));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrganizationResponse> update(
        @PathVariable UUID id,
        @RequestBody @Valid UpdateOrganizationRequest request,
        @CurrentUser UUID requesterId
    ) {
        this.authorizationApi.requirePermission(id, requesterId, PermissionArea.ORGANIZATIONS, AccessLevel.ADMIN);
        this.commandService.handle(request.toCommand(id, requesterId));

        return ResponseEntity.ok(
                OrganizationResponse.from(this.queryService.handle(new GetOrganizationQuery(id))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
        @PathVariable UUID id,
        @CurrentUser UUID requesterId
    ) {
        this.authorizationApi.requirePermission(id, requesterId, PermissionArea.ORGANIZATIONS, AccessLevel.ADMIN);
        this.commandService.handle(new DeleteOrganizationCommand(id, requesterId));
        return ResponseEntity.noContent().build();
    }

    private void requireSelf(UUID requesterId, UUID targetProfileId) {
        if (!requesterId.equals(targetProfileId)) {
            throw new SelfAccessRequiredException();
        }
    }
}
