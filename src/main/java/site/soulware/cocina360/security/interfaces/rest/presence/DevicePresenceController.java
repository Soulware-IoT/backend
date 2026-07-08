package site.soulware.cocina360.security.interfaces.rest.presence;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import site.soulware.cocina360.organizations.interfaces.acl.AccessLevel;
import site.soulware.cocina360.organizations.interfaces.acl.AuthorizationApi;
import site.soulware.cocina360.organizations.interfaces.acl.OrganizationsApi;
import site.soulware.cocina360.organizations.interfaces.acl.PermissionArea;
import site.soulware.cocina360.security.application.edgedevice.EdgeDeviceQueryService;
import site.soulware.cocina360.security.application.edgedevice.EdgeDeviceResult;
import site.soulware.cocina360.security.application.iotdevice.IoTDeviceQueryService;
import site.soulware.cocina360.security.domain.model.exception.EdgeDeviceNotFoundException;
import site.soulware.cocina360.security.domain.model.query.GetEdgeDeviceByOrganizationQuery;
import site.soulware.cocina360.security.domain.model.query.ListDevicesByOrganizationQuery;
import site.soulware.cocina360.security.interfaces.rest.presence.response.DevicePresenceResponse;
import site.soulware.cocina360.shared.infrastructure.auth.CurrentUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Point-in-time presence snapshot: for each of the organization's edge and IoT devices,
 * its current online/offline status. A device that has never connected still appears
 * (as {@code OFFLINE}) rather than being silently absent, by cross-referencing the
 * organization's actual device lists against {@link DevicePresenceRegistry}.
 *
 * <p>Intended to be fetched once on load; {@link DevicePresenceStreamController} then
 * carries live transitions from that point on.
 */
@Tag(name = "device-presence-controller")
@RestController
public class DevicePresenceController {

    private final OrganizationsApi organizationsApi;
    private final AuthorizationApi authorizationApi;
    private final EdgeDeviceQueryService edgeDeviceQueryService;
    private final IoTDeviceQueryService iotDeviceQueryService;
    private final DevicePresenceRegistry registry;

    public DevicePresenceController(
        OrganizationsApi organizationsApi,
        AuthorizationApi authorizationApi,
        EdgeDeviceQueryService edgeDeviceQueryService,
        IoTDeviceQueryService iotDeviceQueryService,
        DevicePresenceRegistry registry
    ) {
        this.organizationsApi = organizationsApi;
        this.authorizationApi = authorizationApi;
        this.edgeDeviceQueryService = edgeDeviceQueryService;
        this.iotDeviceQueryService = iotDeviceQueryService;
        this.registry = registry;
    }

    @GetMapping("/organizations/{organizationId}/devices/presence")
    public ResponseEntity<List<DevicePresenceResponse>> snapshot(
        @PathVariable UUID organizationId,
        @CurrentUser UUID requesterId
    ) {
        this.organizationsApi.requireOrganizationId(organizationId);
        this.authorizationApi.requirePermission(
                organizationId, requesterId, PermissionArea.SECURITY, AccessLevel.ASSIGNEE);

        Map<UUID, DevicePresenceResponse> known = this.registry.snapshot(organizationId).stream()
                .collect(Collectors.toMap(DevicePresenceResponse::deviceId, Function.identity()));

        List<DevicePresenceResponse> result = new ArrayList<>();
        this.findEdge(organizationId).ifPresent(edge -> result.add(
                known.getOrDefault(edge.edgeDeviceId(), this.offline(edge.edgeDeviceId(), edge.code(), DeviceKind.EDGE))));
        this.iotDeviceQueryService.handle(new ListDevicesByOrganizationQuery(organizationId)).forEach(device ->
                result.add(known.getOrDefault(
                        device.deviceId(), this.offline(device.deviceId(), device.code(), DeviceKind.IOT))));

        return ResponseEntity.ok(result);
    }

    private Optional<EdgeDeviceResult> findEdge(UUID organizationId) {
        try {
            return Optional.of(this.edgeDeviceQueryService.handle(
                    new GetEdgeDeviceByOrganizationQuery(organizationId)));
        } catch (EdgeDeviceNotFoundException ex) {
            return Optional.empty();
        }
    }

    private DevicePresenceResponse offline(UUID deviceId, String code, DeviceKind kind) {
        return DevicePresenceResponse.from(deviceId, code, kind, PresenceStatus.OFFLINE, null);
    }
}
