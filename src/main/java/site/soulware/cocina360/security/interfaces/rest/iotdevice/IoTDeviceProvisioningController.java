package site.soulware.cocina360.security.interfaces.rest.iotdevice;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import site.soulware.cocina360.security.application.iotdevice.IoTDeviceCommandService;
import site.soulware.cocina360.security.interfaces.rest.iotdevice.response.ProvisionedDeviceResponse;

/**
 * Factory provisioning utility — mints a device's code + apiKey to burn into firmware.
 * <p>
 * This is a system/admin endpoint: it takes no requester identity and is served under
 * the {@code /internal} prefix so the public API gateway can exclude it by path. It must
 * never be exposed to end users — its response contains the device's secret apiKey.
 * <p>
 * Gated by {@code app.provisioning.enabled} (default {@code false}): where the flag is off the
 * controller is not a bean, so its route is never registered and {@code /internal/iot-devices/**}
 * returns 404 — the endpoint does not exist in that environment, not merely hidden.
 */
@RestController
@ConditionalOnProperty(name = "app.provisioning.enabled", havingValue = "true")
@Tag(name="iot-device-provisioning-controller")
@RequestMapping("/internal/iot-devices")
public class IoTDeviceProvisioningController {

    private final IoTDeviceCommandService commandService;

    public IoTDeviceProvisioningController(IoTDeviceCommandService commandService) {
        this.commandService = commandService;
    }

    @PostMapping("/provision")
    public ResponseEntity<ProvisionedDeviceResponse> provision() {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProvisionedDeviceResponse.from(this.commandService.provision()));
    }
}
