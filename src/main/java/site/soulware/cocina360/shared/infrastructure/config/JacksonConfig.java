package site.soulware.cocina360.shared.infrastructure.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * Centralizes enum JSON representation so domain enums stay free of serialization annotations
 * ({@code @JsonValue}/{@code label()}): every enum is written in lowercase by its {@code name()}.
 * Spring Boot auto-registers any {@code Module} bean into the application {@code ObjectMapper}.
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
    private static final class LowercaseEnumSerializer extends JsonSerializer<Enum> {

        @Override
        public void serialize(Enum value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(value.name().toLowerCase());
        }
    }
}
