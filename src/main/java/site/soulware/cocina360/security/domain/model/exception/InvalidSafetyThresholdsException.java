package site.soulware.cocina360.security.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.BusinessRuleViolationException;

public class InvalidSafetyThresholdsException extends BusinessRuleViolationException {

    private InvalidSafetyThresholdsException(String rule) {
        super(rule);
    }

    public static InvalidSafetyThresholdsException temperature(int warn, int crit) {
        return new InvalidSafetyThresholdsException("error.safety_thresholds.invalid_temperature");
    }

    public static InvalidSafetyThresholdsException gas(double warn, double crit) {
        return new InvalidSafetyThresholdsException("error.safety_thresholds.invalid_gas");
    }
}
