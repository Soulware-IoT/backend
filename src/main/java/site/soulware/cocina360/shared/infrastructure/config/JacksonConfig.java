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

    /**
     * Exposes the lowercase-enum serializer as a {@link SimpleModule} bean. Spring Boot 4 collects
     * every {@code tools.jackson.databind.JacksonModule} bean (which {@code SimpleModule} extends)
     * and registers it into the application {@code JsonMapper} via its
     * {@code StandardJsonMapperBuilderCustomizer} — so simply declaring this bean wires the
     * serializer into all REST (de)serialization without touching the mapper directly. The
     * serializer is registered against {@code Enum.class}: Jackson resolves it for every concrete
     * enum via supertype lookup, making the lowercase rule apply uniformly to all enums.
     *
     * @return the module carrying the global enum serializer
     */
    @Bean
    SimpleModule lowercaseEnumModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Enum.class, new LowercaseEnumSerializer());
        return module;
    }

    /**
     * Writes any enum as its {@code name()} lowercased. {@code @SuppressWarnings("rawtypes")} is
     * needed because the serializer targets the raw {@code Enum} base type (there is no single
     * generic enum type to parameterize). Reads are handled separately: the inverse is the
     * {@code spring.jackson.mapper.accept-case-insensitive-enums} property, which lets the lowercase
     * wire value bind back to the upper-case Java constant.
     */
    @SuppressWarnings("rawtypes")
    private static final class LowercaseEnumSerializer extends ValueSerializer<Enum> {

        /**
         * @param value the enum being serialized
         * @param gen   the Jackson 3 generator the lowercase token is written to
         * @param ctxt  the active serialization context (unused; the rule is value-independent)
         */
        @Override
        public void serialize(Enum value, JsonGenerator gen, SerializationContext ctxt) throws JacksonException {
            gen.writeString(value.name().toLowerCase());
        }
    }
}
