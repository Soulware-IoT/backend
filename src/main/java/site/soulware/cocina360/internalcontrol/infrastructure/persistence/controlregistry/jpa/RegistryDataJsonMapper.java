package site.soulware.cocina360.internalcontrol.infrastructure.persistence.controlregistry.jpa;

import org.springframework.stereotype.Component;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

/**
 * (De)serializes a control registry's captured values to/from the {@code data} jsonb column. The
 * values are a free-form {@code Map<String, Object>} keyed by the format's field keys — there is no
 * fixed schema, so it round-trips as a generic JSON object.
 */
@Component
public class RegistryDataJsonMapper {

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    private final ObjectMapper objectMapper;

    public RegistryDataJsonMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String toJson(Map<String, Object> data) {
        return this.objectMapper.writeValueAsString(data);
    }

    public Map<String, Object> toDomain(String json) {
        if (json == null || json.isBlank()) {
            return Map.of();
        }
        return this.objectMapper.readValue(json, MAP_TYPE);
    }
}
