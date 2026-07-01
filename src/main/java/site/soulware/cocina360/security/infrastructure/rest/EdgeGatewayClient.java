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
    private final String gatewayUrl;

    public EdgeGatewayClient(@Value("${edge.gateway.url}") String gatewayUrl) {
        this.restClient = RestClient.create();
        this.gatewayUrl = gatewayUrl;
    }

    public void sendServoCommand(String edgeAppIp, UUID iotDeviceId, String command) {
        String url = this.gatewayUrl + "/servo";
        String payload = "{\"edgeAppIp\":\"" + edgeAppIp + "\",\"iotDeviceId\":\"" + iotDeviceId + "\",\"command\":\"" + command + "\"}";
        try {
            this.restClient.post()
                    .uri(url)
                    .header("Content-Type", "application/json")
                    .body(payload)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("Failed to send servo command via edge gateway at {}: {}", url, e.getMessage());
            throw new RuntimeException("Edge gateway unreachable at " + this.gatewayUrl, e);
        }
    }
}
