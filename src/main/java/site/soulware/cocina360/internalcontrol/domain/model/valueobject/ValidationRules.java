package site.soulware.cocina360.internalcontrol.domain.model.valueobject;

import site.soulware.cocina360.shared.domain.model.valueobject.ValueObject;

import java.util.List;

public sealed interface ValidationRules extends ValueObject
        permits ValidationRules.None, ValidationRules.Text, ValidationRules.Number, ValidationRules.Select {

    record None() implements ValidationRules {}

    record Text(Integer minLength, Integer maxLength, String pattern) implements ValidationRules {}

    record Number(Double min, Double max) implements ValidationRules {}

    record Select(List<String> options) implements ValidationRules {}
}
