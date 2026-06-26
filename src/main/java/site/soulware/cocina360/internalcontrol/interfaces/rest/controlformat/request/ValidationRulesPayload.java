package site.soulware.cocina360.internalcontrol.interfaces.rest.controlformat.request;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.NumberKind;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ValidationRules;

import java.util.List;

/**
 * Wire representation of {@link ValidationRules}, carrying an explicit {@code kind} discriminator so
 * the polymorphic rules round-trip over JSON without the domain type needing Jackson annotations.
 * Shared by the field request and response payloads.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "kind")
@JsonSubTypes({
    @JsonSubTypes.Type(value = ValidationRulesPayload.None.class, name = "none"),
    @JsonSubTypes.Type(value = ValidationRulesPayload.Text.class, name = "text"),
    @JsonSubTypes.Type(value = ValidationRulesPayload.Number.class, name = "number"),
    @JsonSubTypes.Type(value = ValidationRulesPayload.Select.class, name = "select")
})
public sealed interface ValidationRulesPayload
        permits ValidationRulesPayload.None, ValidationRulesPayload.Text,
        ValidationRulesPayload.Number, ValidationRulesPayload.Select {

    ValidationRules toDomain();

    record None() implements ValidationRulesPayload {
        @Override
        public ValidationRules toDomain() {
            return new ValidationRules.None();
        }
    }

    record Text(Integer minLength, Integer maxLength, String pattern) implements ValidationRulesPayload {
        @Override
        public ValidationRules toDomain() {
            return new ValidationRules.Text(this.minLength, this.maxLength, this.pattern);
        }
    }

    record Number(NumberKind numberKind, Double min, Double max) implements ValidationRulesPayload {
        @Override
        public ValidationRules toDomain() {
            return new ValidationRules.Number(this.numberKind, this.min, this.max);
        }
    }

    record Select(List<String> options) implements ValidationRulesPayload {
        @Override
        public ValidationRules toDomain() {
            return new ValidationRules.Select(this.options);
        }
    }

    static ValidationRulesPayload from(ValidationRules rules) {
        return switch (rules) {
            case ValidationRules.Text t -> new Text(t.minLength(), t.maxLength(), t.pattern());
            case ValidationRules.Number n -> new Number(n.kind(), n.min(), n.max());
            case ValidationRules.Select s -> new Select(s.options());
            case ValidationRules.None() -> new None();
        };
    }
}
