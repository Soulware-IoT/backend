package site.soulware.cocina360.security.interfaces.rest.presence;

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
 * Live presence stream: pushes every online/offline transition among the organization's
 * edge and IoT devices as Server-Sent Events (event name {@code presence}). Independent
 * of the readings stream — a client may subscribe to either, both, or neither. The
 * connection stays open indefinitely, kept alive by heartbeat comments.
 *
 * <p>Only carries future transitions; fetch {@link DevicePresenceController} once on
 * load for the current snapshot.
 */
@Tag(name = "device-presence-stream-controller")
@RestController
public class DevicePresenceStreamController {

    private final OrganizationsApi organizationsApi;
    private final AuthorizationApi authorizationApi;
    private final OrganizationSseHub hub;

    public DevicePresenceStreamController(
        OrganizationsApi organizationsApi,
        AuthorizationApi authorizationApi,
        OrganizationSseHub hub
    ) {
        this.organizationsApi = organizationsApi;
        this.authorizationApi = authorizationApi;
        this.hub = hub;
    }

    @GetMapping(
        value = "/organizations/{organizationId}/devices/presence/stream",
        produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public SseEmitter stream(
        @PathVariable UUID organizationId,
        @CurrentUser UUID requesterId
    ) {
        this.organizationsApi.requireOrganizationId(organizationId);
        this.authorizationApi.requirePermission(
                organizationId, requesterId, PermissionArea.SECURITY, AccessLevel.ASSIGNEE);
        return this.hub.subscribe(organizationId, DevicePresenceRegistry.TOPIC);
    }
}
