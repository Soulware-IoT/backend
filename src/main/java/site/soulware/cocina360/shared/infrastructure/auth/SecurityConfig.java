package site.soulware.cocina360.shared.infrastructure.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import site.soulware.cocina360.shared.infrastructure.rest.i18n.MessageResolver;
import site.soulware.cocina360.shared.infrastructure.rest.response.ErrorResponse;

import tools.jackson.databind.ObjectMapper;

/**
 * Spring Security configuration. The API gateway now only forwards requests (it no longer
 * validates the JWT), so this service is a JWT resource server: it verifies the Supabase
 * token's signature (ES256, via the project's JWKS — see {@code spring.security.oauth2
 * .resourceserver.jwt.jwk-set-uri}) and expiry on every request.
 * <p>
 * Authentication only. Fine-grained, DB-backed authorization (per organization + context) is
 * enforced inside controllers via {@code AuthorizationApi} so it stays live (anti-staleness)
 * and reuses the domain exceptions / i18n envelope.
 */
@Configuration
public class SecurityConfig {

    /**
     * Machine-to-machine endpoints authenticated by {@code X-Edge-Api-Key}, not JWT, plus the
     * OpenAPI docs — all excluded from the JWT filter.
     */
    private static final String[] PUBLIC_PATHS = {
        "/edge/**",
        "/internal/**",
        "/v3/api-docs/**",
        "/swagger-ui/**",
        "/swagger-ui.html"
    };

    @Bean
    public JwtDecoder jwtDecoder(
        @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}") String jwkSetUri
    ) {
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri)
                .jwsAlgorithm(SignatureAlgorithm.ES256)
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
        HttpSecurity http,
        MessageResolver messages,
        ObjectMapper objectMapper
    ) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(PUBLIC_PATHS).permitAll()
                .anyRequest().authenticated())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}))
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(writeError(messages, objectMapper,
                        HttpStatus.UNAUTHORIZED, "Unauthorized", "error.auth.unauthenticated"))
                .accessDeniedHandler((request, response, denied) -> write(response, messages, objectMapper,
                        HttpStatus.FORBIDDEN, "Forbidden", "error.authz.insufficient_permission")));
        return http.build();
    }

    private static org.springframework.security.web.AuthenticationEntryPoint writeError(
        MessageResolver messages,
        ObjectMapper objectMapper,
        HttpStatus status,
        String error,
        String messageKey
    ) {
        return (request, response, authException) ->
                write(response, messages, objectMapper, status, error, messageKey);
    }

    private static void write(
        jakarta.servlet.http.HttpServletResponse response,
        MessageResolver messages,
        ObjectMapper objectMapper,
        HttpStatus status,
        String error,
        String messageKey
    ) throws java.io.IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ErrorResponse body = ErrorResponse.of(status.value(), error, messages.get(messageKey));
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
