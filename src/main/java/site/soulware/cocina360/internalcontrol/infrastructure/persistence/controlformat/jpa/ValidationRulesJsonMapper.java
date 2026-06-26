package site.soulware.cocina360.internalcontrol.infrastructure.persistence.controlformat.jpa;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.stereotype.Component;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.NumberKind;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ValidationRules;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

/**
 * (De)serializes the {@link ValidationRules} sealed hierarchy to/from the {@code validation_rules}
 * jsonb column, keeping the domain type free of Jackson annotations. The JSON carries an explicit
 * {@code kind} discriminator ({@code none|text|number|select}) so it round-trips without relying on
 * the field's {@code type}. Nulls are omitted to keep stored documents compact.
 */
@Component
public class ValidationRulesJsonMapper {

    private final ObjectMapper objectMapper;

    public ValidationRulesJsonMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String toJson(ValidationRules rules) {
        Document document = switch (rules) {
            case ValidationRules.Text t -> new Document("text", t.minLength(), t.maxLength(), t.pattern(), null, null, null, null);
            case ValidationRules.Number n -> new Document("number", null, null, null, n.kind(), n.min(), n.max(), null);
            case ValidationRules.Select s -> new Document("select", null, null, null, null, null, null, s.options());
            case ValidationRules.None() -> new Document("none", null, null, null, null, null, null, null);
        };
        return this.objectMapper.writeValueAsString(document);
    }

    public ValidationRules toDomain(String json) {
        if (json == null || json.isBlank()) {
            return new ValidationRules.None();
        }
        Document document = this.objectMapper.readValue(json, Document.class);
        String kind = document.kind() == null ? "none" : document.kind();
        return switch (kind) {
            case "text" -> new ValidationRules.Text(document.minLength(), document.maxLength(), document.pattern());
            case "number" -> new ValidationRules.Number(document.numberKind(), document.min(), document.max());
            case "select" -> new ValidationRules.Select(document.options());
            default -> new ValidationRules.None();
        };
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    record Document(
        String kind,
        Integer minLength,
        Integer maxLength,
        String pattern,
        NumberKind numberKind,
        Double min,
        Double max,
        List<String> options
    ) {}
}
