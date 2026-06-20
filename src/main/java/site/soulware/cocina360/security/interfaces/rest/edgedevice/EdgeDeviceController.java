package site.soulware.cocina360.security.interfaces.rest.edgedevice;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.soulware.cocina360.organizations.interfaces.acl.OrganizationsApi;
import site.soulware.cocina360.profiles.interfaces.acl.ProfilesApi;
import site.soulware.cocina360.security.application.edgedevice.EdgeDeviceCommandService;
import site.soulware.cocina360.security.application.edgedevice.EdgeDeviceQueryService;
import site.soulware.cocina360.security.domain.model.query.GetEdgeDeviceByOrganizationQuery;
import site.soulware.cocina360.security.domain.model.query.GetEdgeDeviceQuery;
import site.soulware.cocina360.security.domain.model.command.ActivateEdgeDeviceCommand;
import site.soulware.cocina360.security.domain.model.command.DeactivateEdgeDeviceCommand;
import site.soulware.cocina360.security.domain.model.valueobject.EdgeDeviceId;
import site.soulware.cocina360.security.interfaces.rest.edgedevice.request.ClaimEdgeDeviceRequest;
import site.soulware.cocina360.security.interfaces.rest.edgedevice.request.RenameEdgeDeviceRequest;
import site.soulware.cocina360.security.interfaces.rest.edgedevice.response.EdgeDeviceResponse;

import java.util.UUID;

/**
 * The edge device is a 1:1 resource owned by an organization, so claiming and org-scoped
 * lookup are nested under {@code /organizations/{organizationId}/edge-device}. The edge
 * device is first minted by the factory provisioning endpoint; the owner then claims it
 * here by its {@code code}. A direct by-id lookup is also exposed for management/admin.
 */
@RestController
public class EdgeDeviceController {

    private final EdgeDeviceCommandService commandService;
    private final EdgeDeviceQueryService queryService;
    private final OrganizationsApi organizationsApi;
    private final ProfilesApi profilesApi;

    public EdgeDeviceController(
        EdgeDeviceCommandService commandService,
        EdgeDeviceQueryService queryService,
        OrganizationsApi organizationsApi,
        ProfilesApi profilesApi
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.organizationsApi = organizationsApi;
        this.profilesApi = profilesApi;
    }

    @PostMapping("/organizations/{organizationId}/edge-device")
    public ResponseEntity<EdgeDeviceResponse> claim(
        @PathVariable UUID organizationId,
        @RequestBody @Valid ClaimEdgeDeviceRequest request,
        @RequestHeader("X-Requester-Id") UUID requesterId
    ) {
        this.profilesApi.requireProfileId(requesterId);
        this.organizationsApi.requireOrganizationId(organizationId);
        EdgeDeviceId edgeDeviceId = this.commandService.handle(request.toCommand(organizationId, requesterId));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(EdgeDeviceResponse.from(this.queryService.handle(new GetEdgeDeviceQuery(edgeDeviceId.value()))));
    }

    @GetMapping("/organizations/{organizationId}/edge-device")
    public ResponseEntity<EdgeDeviceResponse> getByOrganization(@PathVariable UUID organizationId) {
        this.organizationsApi.requireOrganizationId(organizationId);
        return ResponseEntity.ok(
                EdgeDeviceResponse.from(this.queryService.handle(new GetEdgeDeviceByOrganizationQuery(organizationId))));
    }

    @GetMapping("/edge-devices/{id}")
    public ResponseEntity<EdgeDeviceResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                EdgeDeviceResponse.from(this.queryService.handle(new GetEdgeDeviceQuery(id))));
    }

    @PatchMapping("/edge-devices/{id}/name")
    public ResponseEntity<EdgeDeviceResponse> rename(
        @PathVariable UUID id,
        @RequestBody @Valid RenameEdgeDeviceRequest request,
        @RequestHeader("X-Requester-Id") UUID requesterId
    ) {
        this.profilesApi.requireProfileId(requesterId);
        this.queryService.handle(new GetEdgeDeviceQuery(id));

        this.commandService.handle(request.toCommand(id, requesterId));

        return ResponseEntity.ok(
                EdgeDeviceResponse.from(this.queryService.handle(new GetEdgeDeviceQuery(id))));
    }

    @PostMapping("/edge-devices/{id}/activate")
    public ResponseEntity<EdgeDeviceResponse> activate(
        @PathVariable UUID id,
        @RequestHeader("X-Requester-Id") UUID requesterId
    ) {
        this.profilesApi.requireProfileId(requesterId);
        this.queryService.handle(new GetEdgeDeviceQuery(id));

        this.commandService.handle(new ActivateEdgeDeviceCommand(id, requesterId));

        return ResponseEntity.ok(
                EdgeDeviceResponse.from(this.queryService.handle(new GetEdgeDeviceQuery(id))));
    }

    @PostMapping("/edge-devices/{id}/deactivate")
    public ResponseEntity<EdgeDeviceResponse> deactivate(
        @PathVariable UUID id,
        @RequestHeader("X-Requester-Id") UUID requesterId
    ) {
        this.profilesApi.requireProfileId(requesterId);
        this.queryService.handle(new GetEdgeDeviceQuery(id));

        this.commandService.handle(new DeactivateEdgeDeviceCommand(id, requesterId));

        return ResponseEntity.ok(
                EdgeDeviceResponse.from(this.queryService.handle(new GetEdgeDeviceQuery(id))));
    }
}
