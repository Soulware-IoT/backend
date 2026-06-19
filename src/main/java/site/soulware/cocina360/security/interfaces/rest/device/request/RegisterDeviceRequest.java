package site.soulware.cocina360.security.interfaces.rest.device.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import site.soulware.cocina360.security.domain.model.command.RegisterDeviceCommand;
import site.soulware.cocina360.security.domain.model.valueobject.SafetyThresholds;

import java.util.UUID;

/**
 * @param thresholds optional calibration limits; when omitted the device's hardcoded
 *                   defaults (35/50 °C, 1000/3000 PPM) are applied at registration.
 */
public record RegisterDeviceRequest(
    @NotBlank String code,
    @NotBlank String name,
    @Schema(description = "Optional. Calibration limits; omit entirely to apply the standard "
            + "defaults (35/50 °C, 1000/3000 PPM). If provided, all four values are required.")
    Thresholds thresholds
) {

    public record Thresholds(
        int warnTemperatureC,
        int critTemperatureC,
        double warnGasPpm,
        double critGasPpm
    ) {}

    public RegisterDeviceCommand toCommand(UUID organizationId, UUID requesterId) {
        SafetyThresholds thresholds = this.thresholds == null
                ? null
                : new SafetyThresholds(
                        this.thresholds.warnTemperatureC(),
                        this.thresholds.critTemperatureC(),
                        this.thresholds.warnGasPpm(),
                        this.thresholds.critGasPpm());
        return new RegisterDeviceCommand(organizationId, this.code, this.name, thresholds, requesterId);
    }
}
