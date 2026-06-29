package site.soulware.cocina360.security.infrastructure.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.UUID;

@Component
public class EdgeGatewayClient {

    private static final Logger log = LoggerFactory.getLogger(EdgeGatewayClient.class);

    private final RestClient restClient;
    private final int port;

    public EdgeGatewayClient(@Value("${edge.gateway.port}") int port) {
        this.restClient = RestClient.create();
        this.port = port;
    }

    public void sendServoCommand(String ip, UUID deviceId, String command) {
        String url = "http://" + ip + ":" + this.port + "/servo";
        String payload = "{\"deviceId\":\"" + deviceId + "\",\"command\":\"" + command + "\"}";
        try {
            this.restClient.post()
                    .uri(url)
                    .header("Content-Type", "application/json")
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Failed to send servo command to edge gateway at {}: {}", url, e.getMessage());
            throw new RuntimeException("Edge gateway unreachable at " + ip, e);
        }
    }
}
