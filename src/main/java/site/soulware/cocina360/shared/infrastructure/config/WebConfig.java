package site.soulware.cocina360.shared.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring MVC web-layer configuration. Implementing {@link WebMvcConfigurer} (rather than
 * {@code @EnableWebMvc}) augments Boot's auto-configured MVC instead of replacing it, so only the
 * overridden hooks take effect and all Boot defaults (message converters, the Jackson mapper, etc.)
 * stay in place. Currently its sole concern is the global CORS policy.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Declares the browser CORS policy for the whole API. Without this, a browser on the allowed
     * origins would have its cross-origin calls blocked by the same-origin policy (and preflight
     * {@code OPTIONS} requests would fail). Note this is CORS only — authentication/authorization
     * is handled upstream by the API gateway and is out of scope here.
     *
     * @param registry the registry Spring MVC passes in to collect CORS mappings
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // apply the policy to every endpoint
        registry.addMapping("/**")
                // origins permitted to call the API from a browser: the deployed frontend and local dev
                .allowedOrigins("https://backend-production-c5e8.up.railway.app", "http://localhost:8080")
                // verbs the REST API uses; OPTIONS is required so CORS preflight succeeds
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                // accept any request header (e.g. Content-Type, Accept-Language used for i18n)
                .allowedHeaders("*")
                // allow credentialed requests (cookies / Authorization header) to cross origins
                .allowCredentials(true);
    }
}