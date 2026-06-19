package site.soulware.cocina360.security.interfaces.rest.device;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.soulware.cocina360.organizations.interfaces.acl.OrganizationsApi;
import site.soulware.cocina360.profiles.interfaces.acl.ProfilesApi;
import site.soulware.cocina360.security.application.device.DeviceCommandService;
import site.soulware.cocina360.security.application.device.DeviceQueryService;
import site.soulware.cocina360.security.application.edgegateway.EdgeGatewayQueryService;
import site.soulware.cocina360.security.domain.model.query.GetDeviceQuery;
import site.soulware.cocina360.security.domain.model.query.GetEdgeGatewayByOrganizationQuery;
import site.soulware.cocina360.security.domain.model.query.ListDevicesByOrganizationQuery;
import site.soulware.cocina360.security.domain.model.valueobject.DeviceId;
import site.soulware.cocina360.security.interfaces.rest.device.request.RegisterDeviceRequest;
import site.soulware.cocina360.security.interfaces.rest.device.response.DeviceResponse;

import java.util.List;
import java.util.UUID;

/**
 * Devices are org-owned, so registration and org-scoped access are nested under
 * {@code /organizations/{organizationId}/devices}. A direct by-id lookup is also
 * exposed for management/admin access.
 */
@RestController
public class DeviceController {

    private final DeviceCommandService commandService;
    private final DeviceQueryService queryService;
    private final EdgeGatewayQueryService edgeGatewayQueryService;
    private final OrganizationsApi organizationsApi;
    private final ProfilesApi profilesApi;

    public DeviceController(
        DeviceCommandService commandService,
        DeviceQueryService queryService,
        EdgeGatewayQueryService edgeGatewayQueryService,
        OrganizationsApi organizationsApi,
        ProfilesApi profilesApi
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.edgeGatewayQueryService = edgeGatewayQueryService;
        this.organizationsApi = organizationsApi;
        this.profilesApi = profilesApi;
    }

    @PostMapping("/organizations/{organizationId}/devices")
    public ResponseEntity<DeviceResponse> register(
        @PathVariable UUID organizationId,
        @RequestBody @Valid RegisterDeviceRequest request,
        @RequestHeader("X-Requester-Id") UUID requesterId
    ) {
        // The requester, organization, and the org's edge gateway must all exist: a device
        // is served and authenticated by its org's edge, so the edge is registered first.
        this.profilesApi.requireProfileId(requesterId);
        this.organizationsApi.requireOrganizationId(organizationId);
        this.edgeGatewayQueryService.handle(new GetEdgeGatewayByOrganizationQuery(organizationId));

        DeviceId deviceId = this.commandService.handle(request.toCommand(organizationId, requesterId));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(DeviceResponse.from(this.queryService.handle(new GetDeviceQuery(deviceId.value()))));
    }

    @GetMapping("/organizations/{organizationId}/devices")
    public ResponseEntity<List<DeviceResponse>> listByOrganization(@PathVariable UUID organizationId) {
        this.organizationsApi.requireOrganizationId(organizationId);
        List<DeviceResponse> devices = this.queryService
                .handle(new ListDevicesByOrganizationQuery(organizationId)).stream()
                .map(DeviceResponse::from)
                .toList();
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/devices/{id}")
    public ResponseEntity<DeviceResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                DeviceResponse.from(this.queryService.handle(new GetDeviceQuery(id))));
    }
}
