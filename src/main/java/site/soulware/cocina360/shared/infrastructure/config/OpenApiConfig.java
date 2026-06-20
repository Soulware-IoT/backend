package site.soulware.cocina360.shared.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

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

        return new OpenAPI().servers(List.of(productionServer, localServer));
    }
}