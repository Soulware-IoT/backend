package site.soulware.cocina360.security.interfaces.rest.edgedevice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import site.soulware.cocina360.security.application.edgedevice.EdgeDeviceCommandService;
import site.soulware.cocina360.security.interfaces.rest.edgedevice.response.ProvisionedEdgeDeviceResponse;

/**
 * Factory provisioning utility — mints an edge device's code + apiKey to write into the
 * edge's configuration.
 * <p>
 * This is a system/admin endpoint: it takes no requester identity and is served under
 * the {@code /internal} prefix so the public API gateway can exclude it by path. It must
 * never be exposed to end users — its response contains the edge device's secret apiKey.
 */
@RestController
@Tag(name = "edge-device-provisioning-controller")
@RequestMapping("/internal/edge-devices")
public class EdgeDeviceProvisioningController {

    private final EdgeDeviceCommandService commandService;

    public EdgeDeviceProvisioningController(EdgeDeviceCommandService commandService) {
        this.commandService = commandService;
    }

    @PostMapping("/provision")
    public ResponseEntity<ProvisionedEdgeDeviceResponse> provision() {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProvisionedEdgeDeviceResponse.from(this.commandService.provision()));
    }
}
