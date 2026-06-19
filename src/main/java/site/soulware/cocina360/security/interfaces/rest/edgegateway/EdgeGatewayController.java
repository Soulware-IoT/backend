package site.soulware.cocina360.security.interfaces.rest.edgegateway;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.soulware.cocina360.organizations.interfaces.acl.OrganizationsApi;
import site.soulware.cocina360.profiles.interfaces.acl.ProfilesApi;
import site.soulware.cocina360.security.application.edgegateway.EdgeGatewayCommandService;
import site.soulware.cocina360.security.application.edgegateway.EdgeGatewayQueryService;
import site.soulware.cocina360.security.domain.model.query.GetEdgeGatewayByOrganizationQuery;
import site.soulware.cocina360.security.domain.model.query.GetEdgeGatewayQuery;
import site.soulware.cocina360.security.domain.model.valueobject.EdgeGatewayId;
import site.soulware.cocina360.security.interfaces.rest.edgegateway.request.RegisterEdgeGatewayRequest;
import site.soulware.cocina360.security.interfaces.rest.edgegateway.response.EdgeGatewayResponse;

import java.util.UUID;

/**
 * The edge gateway is a 1:1 resource owned by an organization, so registration and
 * org-scoped lookup are nested under {@code /organizations/{organizationId}/edge-gateway}.
 * A direct by-id lookup is also exposed for management/admin access.
 */
@RestController
public class EdgeGatewayController {

    private final EdgeGatewayCommandService commandService;
    private final EdgeGatewayQueryService queryService;
    private final OrganizationsApi organizationsApi;
    private final ProfilesApi profilesApi;

    public EdgeGatewayController(
        EdgeGatewayCommandService commandService,
        EdgeGatewayQueryService queryService,
        OrganizationsApi organizationsApi,
        ProfilesApi profilesApi
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.organizationsApi = organizationsApi;
        this.profilesApi = profilesApi;
    }

    @PostMapping("/organizations/{organizationId}/edge-gateway")
    public ResponseEntity<EdgeGatewayResponse> register(
        @PathVariable UUID organizationId,
        @RequestBody @Valid RegisterEdgeGatewayRequest request,
        @RequestHeader("X-Requester-Id") UUID requesterId
    ) {
        this.profilesApi.requireProfileId(requesterId);
        this.organizationsApi.requireOrganizationId(organizationId);
        EdgeGatewayId edgeGatewayId = this.commandService.handle(request.toCommand(organizationId, requesterId));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(EdgeGatewayResponse.from(this.queryService.handle(new GetEdgeGatewayQuery(edgeGatewayId.value()))));
    }

    @GetMapping("/organizations/{organizationId}/edge-gateway")
    public ResponseEntity<EdgeGatewayResponse> getByOrganization(@PathVariable UUID organizationId) {
        this.organizationsApi.requireOrganizationId(organizationId);
        return ResponseEntity.ok(
                EdgeGatewayResponse.from(this.queryService.handle(new GetEdgeGatewayByOrganizationQuery(organizationId))));
    }

    @GetMapping("/edge-gateways/{id}")
    public ResponseEntity<EdgeGatewayResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                EdgeGatewayResponse.from(this.queryService.handle(new GetEdgeGatewayQuery(id))));
    }
}
