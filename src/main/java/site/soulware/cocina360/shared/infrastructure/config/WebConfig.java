package site.soulware.cocina360.shared.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * Web-layer CORS configuration. The browser CORS policy is published as a single
 * {@link CorsConfigurationSource} bean (named {@code corsConfigurationSource}) rather than via
 * {@code WebMvcConfigurer#addCorsMappings}: Spring Security's CORS filter runs <b>before</b> the
 * {@code DispatcherServlet}, so it must own the policy to handle preflight {@code OPTIONS} requests
 * (which carry no JWT) without rejecting them as unauthenticated. Spring Security auto-detects this
 * bean by name (see {@code SecurityConfig#securityFilterChain} → {@code http.cors(...)}), and Spring
 * MVC reuses the same bean for any request that reaches it — one source of truth for both layers.
 */
@Configuration
public class WebConfig {

    private static final Logger log = LoggerFactory.getLogger(WebConfig.class);

    /**
     * Browser origins allowed to call the API cross-origin, resolved <b>per environment</b> from
     * {@code app.cors.allowed-origins} (CSV): in dev the local frontends/tooling, in prod the single
     * deployed frontend (supplied via the {@code CORS_ALLOWED_ORIGINS} env var). These are the
     * <em>caller</em> page origins (the frontend) — never the gateway or the backend itself. In the
     * normal topology browsers reach the backend through the gateway (which owns its own CORS); this
     * policy matters only for any browser that calls the backend directly.
     */
    private final List<String> allowedOrigins;

    public WebConfig(@Value("${app.cors.allowed-origins:}") String allowedOrigins) {
        this.allowedOrigins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toList();
    }

    /**
     * Declares the browser CORS policy for the whole API. Without it, a browser on an allowed origin
     * would have its cross-origin calls blocked by the same-origin policy (and preflight
     * {@code OPTIONS} requests would fail). Note this is CORS only — authentication and authorization
     * are enforced by Spring Security / the {@code AuthorizationApi}, not here. Same-origin callers
     * (e.g. Swagger UI served by this app, whose server URL is relative) bypass CORS entirely.
     *
     * @return the global CORS configuration source consumed by Spring Security and Spring MVC
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(this.allowedOrigins);
        // verbs the REST API uses; OPTIONS is required so CORS preflight succeeds
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        // accept any request header (e.g. Content-Type, Authorization, Accept-Language)
        config.setAllowedHeaders(List.of("*"));
        // allow credentialed requests (Authorization header) to cross origins
        config.setAllowCredentials(true);

        log.info("CORS configured with allowed origins: {}", this.allowedOrigins);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
