package site.soulware.cocina360.shared.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        Server productionServer = new Server()
                .url("https://backend-production-c5e8.up.railway.app")
                .description("Servidor de Producción (Railway)");

        Server localServer = new Server()
                .url("http://localhost:8080")
                .description("Servidor Local");

        return new OpenAPI().servers(List.of(localServer, productionServer));
    }

    /**
     * swagger-core renders enum schemas from the enum's {@code name()} (UPPERCASE) and ignores the
     * lowercase-enum serializer in {@link JacksonConfig}, so the docs would disagree with the real
     * wire format. The enum lists live inline inside each DTO schema's properties, so we walk every
     * component schema recursively and lowercase any {@code enum} we find, realigning the Swagger
     * examples with the actual API.
     */
    @Bean
    @SuppressWarnings({"rawtypes"})
    GlobalOpenApiCustomizer lowercaseEnumSchemas() {
        return openApi -> {
            if (openApi.getComponents() == null || openApi.getComponents().getSchemas() == null) {
                return;
            }
            Set<Schema> visited = Collections.newSetFromMap(new IdentityHashMap<>());
            openApi.getComponents().getSchemas().values().forEach(schema -> lowercaseEnums(schema, visited));
        };
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void lowercaseEnums(Schema schema, Set<Schema> visited) {
        if (schema == null || !visited.add(schema)) {
            return;
        }
        List<?> values = schema.getEnum();
        if (values != null) {
            schema.setEnum(values.stream()
                    .map(value -> value instanceof String s ? s.toLowerCase() : value)
                    .toList());
        }
        if (schema.getProperties() != null) {
            schema.getProperties().values().forEach(property -> lowercaseEnums((Schema) property, visited));
        }
        lowercaseEnums(schema.getItems(), visited);
        if (schema.getAdditionalProperties() instanceof Schema additional) {
            lowercaseEnums(additional, visited);
        }
        lowercaseEnumsAll(schema.getAllOf(), visited);
        lowercaseEnumsAll(schema.getAnyOf(), visited);
        lowercaseEnumsAll(schema.getOneOf(), visited);
    }

    @SuppressWarnings("rawtypes")
    private static void lowercaseEnumsAll(List<Schema> schemas, Set<Schema> visited) {
        if (schemas != null) {
            schemas.forEach(schema -> lowercaseEnums(schema, visited));
        }
    }
}