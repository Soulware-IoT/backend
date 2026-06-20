package site.soulware.cocina360.security.domain.model.valueobject;

import site.soulware.cocina360.security.domain.model.exception.InvalidSafetyThresholdsException;
import site.soulware.cocina360.shared.domain.model.valueobject.ValueObject;

/**
 * The four calibration limits a device applies, owned by this backend and pulled
 * by the edge so it can serve them to the device. Temperatures are in degrees
 * Celsius; gas concentrations are in parts per million (PPM). For each metric the
 * warning limit must be strictly below the critical limit.
 */
public record SafetyThresholds(
    int warnTemperatureC,
    int critTemperatureC,
    double warnGasPpm,
    double critGasPpm
) implements ValueObject {

    public SafetyThresholds {
        if (warnTemperatureC >= critTemperatureC) {
            throw InvalidSafetyThresholdsException.temperature(warnTemperatureC, critTemperatureC);
        }
        if (warnGasPpm >= critGasPpm) {
            throw InvalidSafetyThresholdsException.gas(warnGasPpm, critGasPpm);
        }
    }

    /** The device's hardcoded local fallback limits (35/50 °C, 1000/3000 PPM). */
    public static SafetyThresholds defaults() {
        return new SafetyThresholds(35, 50, 1000.0, 3000.0);
    }
}
