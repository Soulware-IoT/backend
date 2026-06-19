package site.soulware.cocina360.security.interfaces.rest.iotdevice.request;

import jakarta.validation.constraints.NotNull;
import site.soulware.cocina360.security.domain.model.command.UpdateIoTDeviceThresholdsCommand;
import site.soulware.cocina360.security.domain.model.valueobject.SafetyThresholds;

import java.util.UUID;

/**
 * Recalibrate a claimed device's safety thresholds. All four limits are required and
 * fully replace the device's current ones; for each metric the warning limit must be
 * strictly below the critical limit.
 */
public record UpdateThresholdsRequest(
    @NotNull Integer warnTemperatureC,
    @NotNull Integer critTemperatureC,
    @NotNull Double warnGasPpm,
    @NotNull Double critGasPpm
) {

    public UpdateIoTDeviceThresholdsCommand toCommand(UUID deviceId, UUID requesterId) {
        SafetyThresholds thresholds = new SafetyThresholds(
                this.warnTemperatureC, this.critTemperatureC, this.warnGasPpm, this.critGasPpm);
        return new UpdateIoTDeviceThresholdsCommand(deviceId, thresholds, requesterId);
    }
}
