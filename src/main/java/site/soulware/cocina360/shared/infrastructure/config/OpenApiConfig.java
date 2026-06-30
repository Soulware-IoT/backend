package site.soulware.cocina360.shared.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.customizers.GlobalOpenApiCustomizer;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import site.soulware.cocina360.shared.infrastructure.auth.CurrentUser;

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

    static {
        // {@code @CurrentUser} parameters are resolved from the JWT, never sent by the client, so they
        // must not appear as request parameters in the docs. Ignored globally for every endpoint.
        SpringDocUtils.getConfig().addAnnotationsToIgnore(CurrentUser.class);
    }

    /**
     * Supplies the base {@link OpenAPI} document (springdoc fills in paths, schemas, etc.). The single
     * <b>relative</b> server ({@code "/"}) makes every "Try it out" request target the same origin that
     * served the docs, so it works in any environment with no hardcoded host — and, being same-origin,
     * bypasses CORS entirely. The only requirement to test in prod is opening Swagger on the backend's
     * own public URL and pasting a valid JWT via "Authorize".
     *
     * @return an OpenAPI document with the relative server and the bearer JWT scheme
     */
    @Bean
    public OpenAPI customOpenAPI() {
        // Relative server: Swagger calls whatever origin it was loaded from (same-origin, no CORS).
        Server sameOriginServer = new Server()
                .url("/")
                .description("Server which Swagger is being loaded from");

        // Bearer JWT scheme: surfaces the "Authorize" button in Swagger UI so the Supabase token can
        // be pasted once and sent as `Authorization: Bearer <token>` on every "Try it out" request.
        // Applied globally; /edge/** and /internal/** ignore it (they use X-Edge-Api-Key, not JWT).
        String bearerScheme = "bearerAuth";
        SecurityScheme jwtScheme = new SecurityScheme()
                .name(bearerScheme)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT");

        return new OpenAPI()
                .servers(List.of(sameOriginServer))
                .components(new Components().addSecuritySchemes(bearerScheme, jwtScheme))
                .addSecurityItem(new SecurityRequirement().addList(bearerScheme));
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