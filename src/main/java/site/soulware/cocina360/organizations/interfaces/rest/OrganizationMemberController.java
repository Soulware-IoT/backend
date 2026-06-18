package site.soulware.cocina360.organizations.interfaces.rest;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.soulware.cocina360.organizations.application.OrganizationMemberCommandService;
import site.soulware.cocina360.organizations.application.OrganizationMemberQueryService;
import site.soulware.cocina360.organizations.application.OrganizationQueryService;
import site.soulware.cocina360.organizations.domain.model.command.RemoveOrganizationMemberCommand;
import site.soulware.cocina360.organizations.domain.model.query.GetOrganizationMemberQuery;
import site.soulware.cocina360.organizations.domain.model.query.GetOrganizationQuery;
import site.soulware.cocina360.organizations.domain.model.query.ListOrganizationMembersQuery;
import site.soulware.cocina360.organizations.interfaces.rest.request.UpdateMemberPermissionsRequest;
import site.soulware.cocina360.organizations.interfaces.rest.response.OrganizationMemberResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/organizations/{organizationId}/members")
public class OrganizationMemberController {

    private final OrganizationMemberCommandService commandService;
    private final OrganizationMemberQueryService queryService;
    private final OrganizationQueryService organizationQueryService;

    public OrganizationMemberController(
        OrganizationMemberCommandService commandService,
        OrganizationMemberQueryService queryService,
        OrganizationQueryService organizationQueryService
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.organizationQueryService = organizationQueryService;
    }

    @GetMapping
    public ResponseEntity<List<OrganizationMemberResponse>> list(@PathVariable UUID organizationId) {
        this.requireOrganization(organizationId);
        return ResponseEntity.ok(
                this.queryService.handle(new ListOrganizationMembersQuery(organizationId))
                        .stream().map(OrganizationMemberResponse::from).toList());
    }

    @GetMapping("/{memberId}")
    public ResponseEntity<OrganizationMemberResponse> getById(
        @PathVariable UUID organizationId,
        @PathVariable UUID memberId
    ) {

        this.requireOrganization(organizationId);
        return ResponseEntity.ok(
                OrganizationMemberResponse.from(
                        this.queryService.handle(new GetOrganizationMemberQuery(organizationId, memberId))));
    }

    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> remove(
        @PathVariable UUID organizationId,
        @PathVariable UUID memberId
    ) {

        this.requireOrganization(organizationId);
        this.commandService.handle(new RemoveOrganizationMemberCommand(organizationId, memberId));
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{memberId}/permissions")
    public ResponseEntity<OrganizationMemberResponse> updatePermissions(
        @PathVariable UUID organizationId,
        @PathVariable UUID memberId,
        @RequestBody @Valid UpdateMemberPermissionsRequest request
    ) {

        this.requireOrganization(organizationId);
        this.commandService.handle(request.toCommand(organizationId, memberId));

        return ResponseEntity.ok(
                OrganizationMemberResponse.from(
                        this.queryService.handle(new GetOrganizationMemberQuery(organizationId, memberId))));
    }

    private void requireOrganization(UUID organizationId) {
        this.organizationQueryService.handle(new GetOrganizationQuery(organizationId));
    }
}
