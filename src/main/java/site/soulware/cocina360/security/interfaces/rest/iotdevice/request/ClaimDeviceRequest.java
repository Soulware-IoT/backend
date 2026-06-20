package site.soulware.cocina360.security.interfaces.rest.iotdevice.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import site.soulware.cocina360.security.domain.model.command.ClaimDeviceCommand;
import site.soulware.cocina360.security.domain.model.valueobject.SafetyThresholds;

import java.util.UUID;

/**
 * Claim a factory-provisioned device into the organization. The owner supplies the
 * device's {@code code} (read off the device) — the apiKey is already held by the
 * backend from provisioning and is never entered here.
 *
 * @param thresholds optional calibration limits; omit to apply the standard defaults.
 */
public record ClaimDeviceRequest(
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

    public ClaimDeviceCommand toCommand(UUID organizationId, UUID requesterId) {
        SafetyThresholds thresholds = this.thresholds == null
                ? null
                : new SafetyThresholds(
                        this.thresholds.warnTemperatureC(),
                        this.thresholds.critTemperatureC(),
                        this.thresholds.warnGasPpm(),
                        this.thresholds.critGasPpm());
        return new ClaimDeviceCommand(organizationId, this.code, this.name, thresholds, requesterId);
    }
}
