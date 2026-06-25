package site.soulware.cocina360.shared.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueSerializer;
import tools.jackson.databind.module.SimpleModule;

/**
 * Centralizes enum JSON representation so domain enums stay free of serialization annotations
 * ({@code @JsonValue}/{@code label()}): every enum is written in lowercase by its {@code name()}.
 * Written against the Jackson 3 API ({@code tools.jackson.*}) — the engine Spring Boot 4 actually
 * uses — so the module is a {@code tools.jackson.databind.JacksonModule}, which Boot's
 * {@code StandardJsonMapperBuilderCustomizer} auto-registers into the application mapper.
 * Case-insensitive reads are enabled via {@code spring.jackson.mapper.accept-case-insensitive-enums}.
 * This is the boundary-side counterpart to the convention-based enum translation in
 * {@code MessageResolver}.
 */
@Configuration
public class JacksonConfig {

    @Bean
    SimpleModule lowercaseEnumModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Enum.class, new LowercaseEnumSerializer());
        return module;
    }

    @SuppressWarnings("rawtypes")
    private static final class LowercaseEnumSerializer extends ValueSerializer<Enum> {

        @Override
        public void serialize(Enum value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
            gen.writeString(value.name().toLowerCase());
        }
    }
}
