package site.soulware.cocina360.security.interfaces.rest.edge;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.soulware.cocina360.security.application.edgedevice.EdgeDeviceQueryService;
import site.soulware.cocina360.security.application.edgedevice.EdgeDeviceResult;
import site.soulware.cocina360.security.application.iotdevice.IoTDeviceQueryService;
import site.soulware.cocina360.security.application.iotdevice.IoTDeviceRegistryEntry;
import site.soulware.cocina360.security.application.reading.ReadingCommandService;
import site.soulware.cocina360.security.domain.model.query.AuthenticateEdgeQuery;
import site.soulware.cocina360.security.domain.model.query.GetDeviceRegistryQuery;
import site.soulware.cocina360.security.interfaces.rest.edge.request.RecordReadingsRequest;
import site.soulware.cocina360.security.interfaces.rest.edge.response.EdgeIdentityResponse;
import site.soulware.cocina360.security.interfaces.rest.edge.response.EdgeRegistryResponse;
import site.soulware.cocina360.security.interfaces.rest.presence.DeviceKind;
import site.soulware.cocina360.security.interfaces.rest.presence.DevicePresenceRegistry;

import java.util.List;

/**
 * Edge-facing API: endpoints the edge device calls on behalf of the edge application,
 * authenticated by the {@code X-Edge-Api-Key} header (not a user identity).
 */
@RestController
@RequestMapping("/edge")
public class EdgeController {

    public static final String API_KEY_HEADER = "X-Edge-Api-Key";

    private final EdgeDeviceQueryService edgeDeviceQueryService;
    private final IoTDeviceQueryService iotDeviceQueryService;
    private final ReadingCommandService readingCommandService;
    private final DevicePresenceRegistry presenceRegistry;

    public EdgeController(
        EdgeDeviceQueryService edgeDeviceQueryService,
        IoTDeviceQueryService iotDeviceQueryService,
        ReadingCommandService readingCommandService,
        DevicePresenceRegistry presenceRegistry
    ) {
        this.edgeDeviceQueryService = edgeDeviceQueryService;
        this.iotDeviceQueryService = iotDeviceQueryService;
        this.readingCommandService = readingCommandService;
        this.presenceRegistry = presenceRegistry;
    }

    /**
     * Linkage handshake: authenticate the calling edge by its API key and return its
     * identity and the organization it is bound to.
     *
     * @return 200 with the edge identity; 401 if the key is missing or unrecognised.
     */
    @GetMapping("/me")
    public ResponseEntity<EdgeIdentityResponse> me(
        @RequestHeader(name = API_KEY_HEADER, required = false) String apiKey
    ) {
        EdgeDeviceResult edge = this.edgeDeviceQueryService.handle(new AuthenticateEdgeQuery(apiKey));
        this.touchEdge(edge);
        return ResponseEntity.ok(EdgeIdentityResponse.from(edge));
    }

    /**
     * Registry pull: authenticate the calling edge, then return the in-service IoT devices
     * of its organization (with each device's apiKey and thresholds) for local replication.
     * The edge polls this to stay in sync; an unknown key yields 401 before any data.
     *
     * <p>Doubles as the liveness signal for the edge and every device it just confirmed
     * it manages — the edge has no dedicated heartbeat, so this periodic pull is it.
     *
     * @return 200 with the org's device registry; 401 if the key is missing or unrecognised.
     */
    @GetMapping("/registry")
    public ResponseEntity<EdgeRegistryResponse> registry(
        @RequestHeader(name = API_KEY_HEADER, required = false) String apiKey
    ) {
        EdgeDeviceResult edge = this.edgeDeviceQueryService.handle(new AuthenticateEdgeQuery(apiKey));
        this.touchEdge(edge);
        List<IoTDeviceRegistryEntry> devices =
                this.iotDeviceQueryService.handle(new GetDeviceRegistryQuery(edge.organizationId()));
        devices.forEach(device -> this.presenceRegistry.touch(
                edge.organizationId(), device.deviceId(), device.code(), DeviceKind.IOT));
        return ResponseEntity.ok(EdgeRegistryResponse.of(edge.organizationId(), devices));
    }

    /**
     * Reading ingestion: authenticate the calling edge, then record the batch of safety
     * readings it forwarded against its organization's devices. Each reading is matched to
     * one of the organization's devices by code; a CRITICAL reading raises a safety alert.
     *
     * @return 202 once the batch is recorded; 401 if the key is missing or unrecognised;
     *         404 if a reading names a device not in the edge's organization.
     */
    @PostMapping("/readings")
    public ResponseEntity<Void> recordReadings(
        @RequestHeader(name = API_KEY_HEADER, required = false) String apiKey,
        @RequestBody @Valid RecordReadingsRequest request
    ) {
        EdgeDeviceResult edge = this.edgeDeviceQueryService.handle(new AuthenticateEdgeQuery(apiKey));
        this.touchEdge(edge);
        this.readingCommandService.handle(request.toCommand(edge.organizationId()));
        return ResponseEntity.accepted().build();
    }

    private void touchEdge(EdgeDeviceResult edge) {
        this.presenceRegistry.touch(edge.organizationId(), edge.edgeDeviceId(), edge.code(), DeviceKind.EDGE);
    }
}
