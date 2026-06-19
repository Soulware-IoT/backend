package site.soulware.cocina360.security.interfaces.rest.device;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import site.soulware.cocina360.security.application.device.DeviceCommandService;
import site.soulware.cocina360.security.interfaces.rest.device.response.ProvisionedDeviceResponse;

/**
 * Factory provisioning utility — mints a device's code + apiKey to burn into firmware.
 * <p>
 * This is a system/admin endpoint: it takes no requester identity and is served under
 * the {@code /internal} prefix so the public API gateway can exclude it by path. It must
 * never be exposed to end users — its response contains the device's secret apiKey.
 */
@RestController
@RequestMapping("/internal/devices")
public class DeviceProvisioningController {

    private final DeviceCommandService commandService;

    public DeviceProvisioningController(DeviceCommandService commandService) {
        this.commandService = commandService;
    }

    @PostMapping("/provision")
    public ResponseEntity<ProvisionedDeviceResponse> provision() {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProvisionedDeviceResponse.from(this.commandService.provision()));
    }
}
