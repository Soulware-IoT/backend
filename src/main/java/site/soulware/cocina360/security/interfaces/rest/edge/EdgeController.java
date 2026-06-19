package site.soulware.cocina360.security.interfaces.rest.edge;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.soulware.cocina360.security.application.edgedevice.EdgeDeviceQueryService;
import site.soulware.cocina360.security.application.edgedevice.EdgeDeviceResult;
import site.soulware.cocina360.security.application.iotdevice.IoTDeviceQueryService;
import site.soulware.cocina360.security.domain.model.query.AuthenticateEdgeQuery;
import site.soulware.cocina360.security.domain.model.query.GetDeviceRegistryQuery;
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
    private final IoTDeviceQueryService iotDeviceQueryService;

    public EdgeController(
        EdgeDeviceQueryService edgeDeviceQueryService,
        IoTDeviceQueryService iotDeviceQueryService
    ) {
        this.edgeDeviceQueryService = edgeDeviceQueryService;
        this.iotDeviceQueryService = iotDeviceQueryService;
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
        return ResponseEntity.ok(EdgeIdentityResponse.from(
                this.edgeDeviceQueryService.handle(new AuthenticateEdgeQuery(apiKey))));
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
}
