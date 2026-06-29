package site.soulware.cocina360.security.interfaces.rest.edge;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.soulware.cocina360.security.application.edgedevice.EdgeDeviceCommandService;
import site.soulware.cocina360.security.application.edgedevice.EdgeDeviceQueryService;
import site.soulware.cocina360.security.application.edgedevice.EdgeDeviceResult;
import site.soulware.cocina360.security.application.iotdevice.IoTDeviceQueryService;
import site.soulware.cocina360.security.application.reading.ReadingCommandService;
import site.soulware.cocina360.security.domain.model.command.UpdateEdgeDeviceCommand;
import site.soulware.cocina360.security.domain.model.query.AuthenticateEdgeQuery;
import site.soulware.cocina360.security.domain.model.query.GetDeviceRegistryQuery;
import site.soulware.cocina360.security.interfaces.rest.edge.request.RecordReadingsRequest;
import site.soulware.cocina360.security.interfaces.rest.edge.response.EdgeIdentityResponse;
import site.soulware.cocina360.security.interfaces.rest.edge.response.EdgeRegistryResponse;

/**
 * Edge-facing API: endpoints the edge device calls on behalf of the edge application,
 * authenticated by the {@code X-Edge-Api-Key} header (not a user identity).
 */
@RestController
@RequestMapping("/edge")
public class EdgeController {

    public static final String API_KEY_HEADER = "X-Edge-Api-Key";

    private final EdgeDeviceQueryService edgeDeviceQueryService;
    private final EdgeDeviceCommandService edgeDeviceCommandService;
    private final IoTDeviceQueryService iotDeviceQueryService;
    private final ReadingCommandService readingCommandService;

    public EdgeController(
        EdgeDeviceQueryService edgeDeviceQueryService,
        EdgeDeviceCommandService edgeDeviceCommandService,
        IoTDeviceQueryService iotDeviceQueryService,
        ReadingCommandService readingCommandService
    ) {
        this.edgeDeviceQueryService = edgeDeviceQueryService;
        this.edgeDeviceCommandService = edgeDeviceCommandService;
        this.iotDeviceQueryService = iotDeviceQueryService;
        this.readingCommandService = readingCommandService;
    }

    /**
     * Self-registration handshake: authenticate the calling edge by its API key, record
     * its current IP for command routing, and return its identity and bound organization.
     * The edge calls this on startup; the IP is used by the backend to reach the edge
     * when sending device commands (e.g. servo).
     *
     * @return 200 with the edge identity; 401 if the key is missing or unrecognised.
     */
    @PostMapping("/me")
    public ResponseEntity<EdgeIdentityResponse> me(
        @RequestHeader(name = API_KEY_HEADER, required = false) String apiKey,
        HttpServletRequest request
    ) {
        EdgeDeviceResult edge = this.edgeDeviceQueryService.handle(new AuthenticateEdgeQuery(apiKey));
        this.edgeDeviceCommandService.handle(
                new UpdateEdgeDeviceCommand(edge.edgeDeviceId(), null, null, null, request.getRemoteAddr()));
        return ResponseEntity.ok(EdgeIdentityResponse.from(edge));
    }

    /**
     * Registry pull: authenticate the calling edge, then return the in-service IoT devices
     * of its organization (with each device's apiKey and thresholds) for local replication.
     * The edge polls this to stay in sync; an unknown key yields 401 before any data.
     *
     * @return 200 with the org's device registry; 401 if the key is missing or unrecognised.
     */
    @GetMapping("/registry")
    public ResponseEntity<EdgeRegistryResponse> registry(
        @RequestHeader(name = API_KEY_HEADER, required = false) String apiKey
    ) {
        EdgeDeviceResult edge = this.edgeDeviceQueryService.handle(new AuthenticateEdgeQuery(apiKey));
        return ResponseEntity.ok(EdgeRegistryResponse.of(
                edge.organizationId(),
                this.iotDeviceQueryService.handle(new GetDeviceRegistryQuery(edge.organizationId()))));
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
        this.readingCommandService.handle(request.toCommand(edge.organizationId()));
        return ResponseEntity.accepted().build();
    }
}
