package site.soulware.cocina360.security.interfaces.rest.iotdevice;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import site.soulware.cocina360.organizations.interfaces.acl.OrganizationsApi;
import site.soulware.cocina360.profiles.interfaces.acl.ProfilesApi;
import site.soulware.cocina360.security.application.edgedevice.EdgeDeviceQueryService;
import site.soulware.cocina360.security.application.iotdevice.IoTDeviceCommandService;
import site.soulware.cocina360.security.application.iotdevice.IoTDeviceQueryService;
import site.soulware.cocina360.security.domain.model.query.GetIoTDeviceQuery;
import site.soulware.cocina360.security.domain.model.query.GetEdgeDeviceByOrganizationQuery;
import site.soulware.cocina360.security.domain.model.query.ListDevicesByOrganizationQuery;
import site.soulware.cocina360.security.domain.model.valueobject.IoTDeviceId;
import site.soulware.cocina360.security.interfaces.rest.iotdevice.request.ClaimDeviceRequest;
import site.soulware.cocina360.security.interfaces.rest.iotdevice.request.UpdateIoTDeviceRequest;
import site.soulware.cocina360.security.interfaces.rest.iotdevice.response.IoTDeviceResponse;

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
    private final ProfilesApi profilesApi;

    public IoTDeviceController(
        IoTDeviceCommandService commandService,
        IoTDeviceQueryService queryService,
        EdgeDeviceQueryService edgeDeviceQueryService,
        OrganizationsApi organizationsApi,
        ProfilesApi profilesApi
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.edgeDeviceQueryService = edgeDeviceQueryService;
        this.organizationsApi = organizationsApi;
        this.profilesApi = profilesApi;
    }

    @PostMapping("/organizations/{organizationId}/iot-devices")
    public ResponseEntity<IoTDeviceResponse> claim(
        @PathVariable UUID organizationId,
        @RequestBody @Valid ClaimDeviceRequest request,
        @RequestHeader("X-Requester-Id") UUID requesterId
    ) {
        // The requester, organization, and the org's edge device must all exist: a device
        // is served and authenticated by its org's edge, so the edge is registered first.
        this.profilesApi.requireProfileId(requesterId);
        this.organizationsApi.requireOrganizationId(organizationId);
        this.edgeDeviceQueryService.handle(new GetEdgeDeviceByOrganizationQuery(organizationId));

        IoTDeviceId deviceId = this.commandService.handle(request.toCommand(organizationId, requesterId));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(IoTDeviceResponse.from(this.queryService.handle(new GetIoTDeviceQuery(deviceId.value()))));
    }

    @GetMapping("/organizations/{organizationId}/iot-devices")
    public ResponseEntity<List<IoTDeviceResponse>> listByOrganization(@PathVariable UUID organizationId) {
        this.organizationsApi.requireOrganizationId(organizationId);
        List<IoTDeviceResponse> devices = this.queryService
                .handle(new ListDevicesByOrganizationQuery(organizationId)).stream()
                .map(IoTDeviceResponse::from)
                .toList();
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/iot-devices/{id}")
    public ResponseEntity<IoTDeviceResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                IoTDeviceResponse.from(this.queryService.handle(new GetIoTDeviceQuery(id))));
    }

    /**
     * Partial update of a claimed device: any of name, thresholds, and activation status.
     * Omitted fields are left unchanged.
     */
    @PatchMapping("/iot-devices/{id}")
    public ResponseEntity<IoTDeviceResponse> update(
        @PathVariable UUID id,
        @RequestBody @Valid UpdateIoTDeviceRequest request,
        @RequestHeader("X-Requester-Id") UUID requesterId
    ) {
        // The requester and the target device must exist; the command service re-checks the
        // device on write, but verifying here surfaces a 404 before applying the change.
        this.profilesApi.requireProfileId(requesterId);
        this.queryService.handle(new GetIoTDeviceQuery(id));

        this.commandService.handle(request.toCommand(id, requesterId));

        return ResponseEntity.ok(
                IoTDeviceResponse.from(this.queryService.handle(new GetIoTDeviceQuery(id))));
    }
}
