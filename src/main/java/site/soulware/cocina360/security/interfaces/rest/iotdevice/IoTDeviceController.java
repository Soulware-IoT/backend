package site.soulware.cocina360.security.interfaces.rest.iotdevice;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import site.soulware.cocina360.organizations.interfaces.acl.AccessLevel;
import site.soulware.cocina360.organizations.interfaces.acl.AuthorizationApi;
import site.soulware.cocina360.organizations.interfaces.acl.OrganizationsApi;
import site.soulware.cocina360.organizations.interfaces.acl.PermissionArea;
import site.soulware.cocina360.security.application.edgedevice.EdgeDeviceQueryService;
import site.soulware.cocina360.security.application.edgedevice.EdgeDeviceResult;
import site.soulware.cocina360.security.application.iotdevice.IoTDeviceCommandService;
import site.soulware.cocina360.security.application.iotdevice.IoTDeviceQueryService;
import site.soulware.cocina360.security.application.iotdevice.IoTDeviceResult;
import site.soulware.cocina360.security.domain.model.query.GetIoTDeviceQuery;
import site.soulware.cocina360.security.domain.model.query.GetEdgeDeviceByOrganizationQuery;
import site.soulware.cocina360.security.domain.model.query.ListDevicesByOrganizationQuery;
import site.soulware.cocina360.security.domain.model.valueobject.IoTDeviceId;
import site.soulware.cocina360.security.infrastructure.persistence.authz.DeviceOrganizationQuery;
import site.soulware.cocina360.security.infrastructure.rest.EdgeGatewayClient;
import site.soulware.cocina360.security.interfaces.rest.iotdevice.request.ClaimDeviceRequest;
import site.soulware.cocina360.security.interfaces.rest.iotdevice.request.ServoCommandRequest;
import site.soulware.cocina360.security.interfaces.rest.iotdevice.request.UpdateIoTDeviceRequest;
import site.soulware.cocina360.security.interfaces.rest.iotdevice.response.IoTDeviceListResponse;
import site.soulware.cocina360.security.interfaces.rest.iotdevice.response.IoTDeviceResponse;
import site.soulware.cocina360.shared.infrastructure.auth.CurrentUser;
import site.soulware.cocina360.subscriptions.interfaces.acl.SubscriptionsApi;

import java.util.List;
import java.util.UUID;

/**
 * IoTDevices are org-owned, so registration and org-scoped access are nested under
 * {@code /organizations/{organizationId}/iot-devices}. A direct by-id lookup is also
 * exposed for management/admin access.
 */
@Tag(name = "iot-device-controller")
@RestController
public class IoTDeviceController {

    private final IoTDeviceCommandService commandService;
    private final IoTDeviceQueryService queryService;
    private final EdgeDeviceQueryService edgeDeviceQueryService;
    private final OrganizationsApi organizationsApi;
    private final AuthorizationApi authorizationApi;
    private final SubscriptionsApi subscriptionsApi;
    private final DeviceOrganizationQuery deviceOrganizationQuery;
    private final EdgeGatewayClient edgeGatewayClient;

    public IoTDeviceController(
        IoTDeviceCommandService commandService,
        IoTDeviceQueryService queryService,
        EdgeDeviceQueryService edgeDeviceQueryService,
        OrganizationsApi organizationsApi,
        AuthorizationApi authorizationApi,
        DeviceOrganizationQuery deviceOrganizationQuery,
        EdgeGatewayClient edgeGatewayClient,
        SubscriptionsApi subscriptionsApi
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.edgeDeviceQueryService = edgeDeviceQueryService;
        this.organizationsApi = organizationsApi;
        this.authorizationApi = authorizationApi;
        this.subscriptionsApi = subscriptionsApi;
        this.deviceOrganizationQuery = deviceOrganizationQuery;
        this.edgeGatewayClient = edgeGatewayClient;
    }

    @PostMapping("/organizations/{organizationId}/iot-devices")
    public ResponseEntity<IoTDeviceResponse> claim(
        @PathVariable UUID organizationId,
        @RequestBody @Valid ClaimDeviceRequest request,
        @CurrentUser UUID requesterId
    ) {
        this.organizationsApi.requireOrganizationId(organizationId);
        this.authorizationApi.requirePermission(organizationId, requesterId, PermissionArea.SECURITY, AccessLevel.LIEUTENANT);
        this.subscriptionsApi.enforceDeviceQuota(organizationId, this.queryService.countByOrganization(organizationId));
        this.edgeDeviceQueryService.handle(new GetEdgeDeviceByOrganizationQuery(organizationId));

        IoTDeviceId deviceId = this.commandService.handle(request.toCommand(organizationId, requesterId));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(IoTDeviceResponse.from(this.queryService.handle(new GetIoTDeviceQuery(deviceId.value()))));
    }

    @GetMapping("/organizations/{organizationId}/iot-devices")
    public ResponseEntity<IoTDeviceListResponse> listByOrganization(
        @PathVariable UUID organizationId,
        @CurrentUser UUID requesterId
    ) {
        this.organizationsApi.requireOrganizationId(organizationId);
        this.authorizationApi.requirePermission(organizationId, requesterId, PermissionArea.SECURITY, AccessLevel.ASSIGNEE);
        List<IoTDeviceResponse> devices = this.queryService
                .handle(new ListDevicesByOrganizationQuery(organizationId)).stream()
                .map(IoTDeviceResponse::from)
                .toList();
        long used = this.queryService.countByOrganization(organizationId);
        int limit = this.subscriptionsApi.deviceQuotaFor(organizationId);
        return ResponseEntity.ok(new IoTDeviceListResponse(devices, new IoTDeviceListResponse.Quota(used, limit)));
    }

    @GetMapping("/iot-devices/{id}")
    public ResponseEntity<IoTDeviceResponse> getById(
        @PathVariable UUID id,
        @CurrentUser UUID requesterId
    ) {
        this.authorizeByDevice(id, requesterId, AccessLevel.ASSIGNEE);
        return ResponseEntity.ok(
                IoTDeviceResponse.from(this.queryService.handle(new GetIoTDeviceQuery(id))));
    }

    @PostMapping("/iot-devices/{id}/servo")
    public ResponseEntity<Void> servo(
        @PathVariable UUID id,
        @RequestBody @Valid ServoCommandRequest request
    ) {
        IoTDeviceResult device = this.queryService.handle(new GetIoTDeviceQuery(id));
        EdgeDeviceResult edge = this.edgeDeviceQueryService.handle(
                new GetEdgeDeviceByOrganizationQuery(device.organizationId()));
        this.edgeGatewayClient.sendServoCommand(edge.ip(), id, request.command().name().toLowerCase());
        return ResponseEntity.ok().build();
    }

    /**
     * Partial update of a claimed device: any of name, thresholds, and activation status.
     * Omitted fields are left unchanged.
     */
    @PatchMapping("/iot-devices/{id}")
    public ResponseEntity<IoTDeviceResponse> update(
        @PathVariable UUID id,
        @RequestBody @Valid UpdateIoTDeviceRequest request,
        @CurrentUser UUID requesterId
    ) {
        this.authorizeByDevice(id, requesterId, AccessLevel.LIEUTENANT);
        this.queryService.handle(new GetIoTDeviceQuery(id));

        this.commandService.handle(request.toCommand(id, requesterId));

        return ResponseEntity.ok(
                IoTDeviceResponse.from(this.queryService.handle(new GetIoTDeviceQuery(id))));
    }

    private void authorizeByDevice(UUID deviceId, UUID requesterId, AccessLevel minimum) {
        this.deviceOrganizationQuery.findIotDeviceOrganization(deviceId)
                .ifPresent(orgId -> this.authorizationApi.requirePermission(orgId, requesterId, PermissionArea.SECURITY, minimum));
    }
}
