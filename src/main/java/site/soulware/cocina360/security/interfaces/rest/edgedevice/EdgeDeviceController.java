package site.soulware.cocina360.security.interfaces.rest.edgedevice;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.soulware.cocina360.organizations.interfaces.acl.AccessLevel;
import site.soulware.cocina360.organizations.interfaces.acl.AuthorizationApi;
import site.soulware.cocina360.organizations.interfaces.acl.OrganizationsApi;
import site.soulware.cocina360.organizations.interfaces.acl.PermissionArea;
import site.soulware.cocina360.security.application.edgedevice.EdgeDeviceCommandService;
import site.soulware.cocina360.security.application.edgedevice.EdgeDeviceQueryService;
import site.soulware.cocina360.security.domain.model.query.GetEdgeDeviceByOrganizationQuery;
import site.soulware.cocina360.security.domain.model.query.GetEdgeDeviceQuery;
import site.soulware.cocina360.security.domain.model.valueobject.EdgeDeviceId;
import site.soulware.cocina360.security.infrastructure.persistence.authz.DeviceOrganizationQuery;
import site.soulware.cocina360.security.interfaces.rest.edgedevice.request.ClaimEdgeDeviceRequest;
import site.soulware.cocina360.security.interfaces.rest.edgedevice.request.UpdateEdgeDeviceRequest;
import site.soulware.cocina360.security.interfaces.rest.edgedevice.response.EdgeDeviceResponse;
import site.soulware.cocina360.shared.infrastructure.auth.CurrentUser;

import java.util.UUID;

/**
 * The edge device is a 1:1 resource owned by an organization, so claiming and org-scoped
 * lookup are nested under {@code /organizations/{organizationId}/edge-device}. The edge
 * device is first minted by the factory provisioning endpoint; the owner then claims it
 * here by its {@code code}. A direct by-id lookup is also exposed for management/admin.
 */
@RestController
public class EdgeDeviceController {

    private final EdgeDeviceCommandService commandService;
    private final EdgeDeviceQueryService queryService;
    private final OrganizationsApi organizationsApi;
    private final AuthorizationApi authorizationApi;
    private final DeviceOrganizationQuery deviceOrganizationQuery;

    public EdgeDeviceController(
        EdgeDeviceCommandService commandService,
        EdgeDeviceQueryService queryService,
        OrganizationsApi organizationsApi,
        AuthorizationApi authorizationApi,
        DeviceOrganizationQuery deviceOrganizationQuery
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.organizationsApi = organizationsApi;
        this.authorizationApi = authorizationApi;
        this.deviceOrganizationQuery = deviceOrganizationQuery;
    }

    @PostMapping("/organizations/{organizationId}/edge-device")
    public ResponseEntity<EdgeDeviceResponse> claim(
        @PathVariable UUID organizationId,
        @RequestBody @Valid ClaimEdgeDeviceRequest request,
        @CurrentUser UUID requesterId
    ) {
        this.organizationsApi.requireOrganizationId(organizationId);
        this.authorizationApi.requirePermission(organizationId, requesterId, PermissionArea.SECURITY, AccessLevel.LIEUTENANT);
        EdgeDeviceId edgeDeviceId = this.commandService.handle(request.toCommand(organizationId, requesterId));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(EdgeDeviceResponse.from(this.queryService.handle(new GetEdgeDeviceQuery(edgeDeviceId.value()))));
    }

    @GetMapping("/organizations/{organizationId}/edge-device")
    public ResponseEntity<EdgeDeviceResponse> getByOrganization(
        @PathVariable UUID organizationId,
        @CurrentUser UUID requesterId
    ) {
        this.organizationsApi.requireOrganizationId(organizationId);
        this.authorizationApi.requirePermission(organizationId, requesterId, PermissionArea.SECURITY, AccessLevel.ASSIGNEE);
        return ResponseEntity.ok(
                EdgeDeviceResponse.from(this.queryService.handle(new GetEdgeDeviceByOrganizationQuery(organizationId))));
    }

    @GetMapping("/edge-device/{id}")
    public ResponseEntity<EdgeDeviceResponse> getById(
        @PathVariable UUID id,
        @CurrentUser UUID requesterId
    ) {
        this.authorizeByEdgeDevice(id, requesterId, AccessLevel.ASSIGNEE);
        return ResponseEntity.ok(
                EdgeDeviceResponse.from(this.queryService.handle(new GetEdgeDeviceQuery(id))));
    }

    /**
     * Partial update of a claimed edge device: any of name and activation status.
     * Omitted fields are left unchanged.
     */
    @PatchMapping("/edge-device/{id}")
    public ResponseEntity<EdgeDeviceResponse> update(
        @PathVariable UUID id,
        @RequestBody @Valid UpdateEdgeDeviceRequest request,
        @CurrentUser UUID requesterId
    ) {
        this.authorizeByEdgeDevice(id, requesterId, AccessLevel.LIEUTENANT);
        this.queryService.handle(new GetEdgeDeviceQuery(id));

        this.commandService.handle(request.toCommand(id, requesterId));

        return ResponseEntity.ok(
                EdgeDeviceResponse.from(this.queryService.handle(new GetEdgeDeviceQuery(id))));
    }

    /** Resolves the device's owning org (if claimed) and checks the requester's security level. */
    private void authorizeByEdgeDevice(UUID edgeDeviceId, UUID requesterId, AccessLevel minimum) {
        this.deviceOrganizationQuery.findEdgeDeviceOrganization(edgeDeviceId)
                .ifPresent(orgId -> this.authorizationApi.requirePermission(orgId, requesterId, PermissionArea.SECURITY, minimum));
    }
}
