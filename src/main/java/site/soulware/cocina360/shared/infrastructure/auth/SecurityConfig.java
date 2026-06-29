package site.soulware.cocina360.shared.infrastructure.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer;
import org.springframework.security.config.annotation.web.configurers.SessionManagementConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import site.soulware.cocina360.shared.infrastructure.rest.i18n.MessageResolver;
import site.soulware.cocina360.shared.infrastructure.rest.response.ErrorResponse;

import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

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

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private final String jwkSetUri = null;

    private final MessageResolver messages;
    private final ObjectMapper objectMapper;

    public SecurityConfig(MessageResolver messages, ObjectMapper objectMapper) {
        this.messages = messages;
        this.objectMapper = objectMapper;
    }

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
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(this.jwkSetUri)
                .jwsAlgorithm(SignatureAlgorithm.ES256)
                .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(SecurityConfig::disableCsrf)
            .sessionManagement(SecurityConfig::useStatelessSessions)
            .authorizeHttpRequests(SecurityConfig::permitPublicPathsAuthenticateRest)
            .oauth2ResourceServer(SecurityConfig::enableJwtResourceServer)
            .exceptionHandling(this::writeErrorEnvelopeOnAuthFailures);

        return http.build();
    }

    private static void disableCsrf(CsrfConfigurer<HttpSecurity> csrf) {
        csrf.disable();
    }

    private static void useStatelessSessions(SessionManagementConfigurer<HttpSecurity> session) {
        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
    }

    private static void permitPublicPathsAuthenticateRest(
        AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry registry
    ) {
        registry
            .requestMatchers(PUBLIC_PATHS).permitAll()
            .anyRequest().authenticated();
    }

    private static void enableJwtResourceServer(OAuth2ResourceServerConfigurer<HttpSecurity> oauth2) {
        oauth2.jwt(Customizer.withDefaults());
    }

    /** Routes security-layer failures through our standard {@link ErrorResponse} envelope. */
    private void writeErrorEnvelopeOnAuthFailures(ExceptionHandlingConfigurer<HttpSecurity> exceptions) {
        exceptions
            .authenticationEntryPoint(this::respondUnauthenticated)
            .accessDeniedHandler(this::respondForbidden);
    }

    private void respondUnauthenticated(
        HttpServletRequest request,
        HttpServletResponse response,
        AuthenticationException authException
    ) throws IOException {
        this.writeErrorEnvelope(response, HttpStatus.UNAUTHORIZED, "Unauthorized", "error.auth.unauthenticated");
    }

    private void respondForbidden(
        HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException deniedException
    ) throws IOException {
        this.writeErrorEnvelope(response, HttpStatus.FORBIDDEN, "Forbidden", "error.authz.insufficient_permission");
    }

    private void writeErrorEnvelope(
        HttpServletResponse response,
        HttpStatus status,
        String error,
        String messageKey
    ) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ErrorResponse body = ErrorResponse.of(status.value(), error, this.messages.get(messageKey));
        response.getWriter().write(this.objectMapper.writeValueAsString(body));
    }
}
