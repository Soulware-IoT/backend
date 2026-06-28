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

/**
 * springdoc / OpenAPI documentation configuration. Two concerns: declaring the servers shown in
 * Swagger UI's "Try it out", and realigning generated enum schemas with the API's actual lowercase
 * wire format. Both shape only the generated docs — they have no effect on runtime request handling.
 */
@Configuration
public class OpenApiConfig {

    /**
     * Supplies the base {@link OpenAPI} document, seeding only the list of servers (springdoc fills
     * in paths, schemas, etc.). The servers populate the "Servers" dropdown in Swagger UI and
     * determine the base URL each "Try it out" request targets. <b>Order matters:</b> Swagger UI
     * selects the first entry as the default, so {@code localServer} is listed first to make local
     * development the default target, with the Railway deployment available as the second option.
     *
     * @return an OpenAPI document carrying the configured server list
     */
    @Bean
    public OpenAPI customOpenAPI() {
        // The deployed (Railway) target — second in the list, so it is the non-default option.
        Server productionServer = new Server()
                .url("https://www.api.cocina360.soulware.site")
                .description("Servidor de Producción (Render)");

        // The local dev target — listed first below, so it is Swagger UI's default selection.
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

    /**
     * Recursively descends a schema tree and lowercases every {@code enum} value list it finds.
     * Enum values are not always on top-level component schemas — they sit inline on the properties
     * of the DTO schemas — so the walk follows {@code properties}, array {@code items}, map
     * {@code additionalProperties}, and the {@code allOf}/{@code anyOf}/{@code oneOf} compositions.
     * The {@code visited} identity set guards against the cycles that {@code $ref}-shared schemas can
     * form, preventing infinite recursion. {@code @SuppressWarnings} covers the raw {@code Schema}
     * (swagger's model type is generic) and the unchecked {@code setEnum} on that raw type.
     *
     * @param schema  the schema node to process (no-op when {@code null} or already visited)
     * @param visited identity set of schemas already processed, shared across the whole walk
     */
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

    /**
     * Helper that applies {@link #lowercaseEnums} to each schema in a composition list
     * ({@code allOf}/{@code anyOf}/{@code oneOf}), tolerating a {@code null} list. Exists only to
     * keep {@link #lowercaseEnums} readable by factoring out the repeated null-check-and-iterate.
     *
     * @param schemas a composition list of sub-schemas, or {@code null} if absent
     * @param visited the shared identity set of already-processed schemas
     */
    @SuppressWarnings("rawtypes")
    private static void lowercaseEnumsAll(List<Schema> schemas, Set<Schema> visited) {
        if (schemas != null) {
            schemas.forEach(schema -> lowercaseEnums(schema, visited));
        }
    }
}