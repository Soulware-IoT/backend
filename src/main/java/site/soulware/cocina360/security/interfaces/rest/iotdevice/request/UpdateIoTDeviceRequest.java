package site.soulware.cocina360.security.interfaces.rest.iotdevice.request;

import io.swagger.v3.oas.annotations.media.Schema;
import site.soulware.cocina360.security.domain.model.command.UpdateIoTDeviceCommand;
import site.soulware.cocina360.security.domain.model.valueobject.SafetyThresholds;

import java.util.UUID;

/**
 * Partial update of a claimed IoT device. Every field is optional — omit a field to leave
 * it unchanged. The device must already be claimed; these operations are not valid while
 * it is still {@code PROVISIONED}.
 *
 * @param name new display name; omit to leave unchanged.
 * @param thresholds new calibration limits (all four required if provided); omit to leave unchanged.
 * @param status {@code ACTIVE} or {@code INACTIVE} to (de)activate; omit to leave unchanged.
 */
public record UpdateIoTDeviceRequest(
    @Schema(description = "Optional. New display name; omit to leave unchanged.")
    String name,
    @Schema(description = "Optional. Calibration limits; omit to leave unchanged. "
            + "If provided, all four values are required (warn must be below crit).")
    Thresholds thresholds,
    @Schema(description = "Optional. ACTIVE or INACTIVE to (de)activate; omit to leave unchanged.")
    Activation status
) {

    public enum Activation { ACTIVE, INACTIVE }

    public record Thresholds(
        Temperature temperature,
        Gas gas
    ) {
        public record Temperature(int warn, int crit) {}
        public record Gas(double warn, double crit) {}
    }

    public UpdateIoTDeviceCommand toCommand(UUID deviceId, UUID requesterId) {
        SafetyThresholds thresholds = this.thresholds == null
                ? null
                : new SafetyThresholds(
                        this.thresholds.temperature().warn(),
                        this.thresholds.temperature().crit(),
                        this.thresholds.gas().warn(),
                        this.thresholds.gas().crit());
        Boolean activate = this.status == null ? null : this.status == Activation.ACTIVE;
        return new UpdateIoTDeviceCommand(deviceId, this.name, thresholds, activate, requesterId);
    }
}
