package site.soulware.cocina360.security.interfaces.rest.reading;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import io.swagger.v3.oas.annotations.tags.Tag;
import site.soulware.cocina360.organizations.interfaces.acl.AccessLevel;
import site.soulware.cocina360.organizations.interfaces.acl.AuthorizationApi;
import site.soulware.cocina360.organizations.interfaces.acl.OrganizationsApi;
import site.soulware.cocina360.organizations.interfaces.acl.PermissionArea;
import site.soulware.cocina360.security.interfaces.rest.sse.OrganizationSseHub;
import site.soulware.cocina360.shared.infrastructure.auth.CurrentUser;

import java.util.UUID;

/**
 * Live telemetry stream: pushes every reading recorded for the organization's devices
 * to the connected client as Server-Sent Events (event name {@code reading}; each
 * payload carries its {@code deviceId} so clients filter per device). The connection
 * stays open indefinitely, kept alive by heartbeat comments.
 */
@Tag(name = "reading-stream-controller")
@RestController
public class ReadingStreamController {

    static final String TOPIC = "readings";

    private final OrganizationsApi organizationsApi;
    private final AuthorizationApi authorizationApi;
    private final OrganizationSseHub hub;

    public ReadingStreamController(
        OrganizationsApi organizationsApi,
        AuthorizationApi authorizationApi,
        OrganizationSseHub hub
    ) {
        this.organizationsApi = organizationsApi;
        this.authorizationApi = authorizationApi;
        this.hub = hub;
    }

    @GetMapping(
        value = "/organizations/{organizationId}/readings/stream",
        produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public SseEmitter stream(
        @PathVariable UUID organizationId,
        @CurrentUser UUID requesterId
    ) {
        this.organizationsApi.requireOrganizationId(organizationId);
        this.authorizationApi.requirePermission(
                organizationId, requesterId, PermissionArea.SECURITY, AccessLevel.ASSIGNEE);
        return this.hub.subscribe(organizationId, TOPIC);
    }
}
