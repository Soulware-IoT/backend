package site.soulware.cocina360.organizations.interfaces.rest;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.soulware.cocina360.organizations.application.OrganizationCommandService;
import site.soulware.cocina360.organizations.application.OrganizationQueryService;
import site.soulware.cocina360.organizations.domain.model.command.DeleteOrganizationCommand;
import site.soulware.cocina360.organizations.domain.model.query.GetOrganizationQuery;
import site.soulware.cocina360.organizations.interfaces.rest.request.CreateOrganizationRequest;
import site.soulware.cocina360.organizations.interfaces.rest.request.UpdateOrganizationRequest;
import site.soulware.cocina360.organizations.interfaces.rest.response.OrganizationResponse;

import java.util.UUID;

@RestController
@RequestMapping("/organizations")
public class OrganizationController {

    private final OrganizationCommandService commandService;
    private final OrganizationQueryService queryService;

    public OrganizationController(
        OrganizationCommandService commandService,
        OrganizationQueryService queryService
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping
    public ResponseEntity<OrganizationResponse> create(
        @RequestBody @Valid CreateOrganizationRequest request,
        @RequestHeader(name = "X-Requester-Id", required = true) UUID requesterId
    ) {
        var organizationId = this.commandService.handle(request.toCommand(requesterId));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(OrganizationResponse.from(this.queryService.handle(new GetOrganizationQuery(organizationId.value()))));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrganizationResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                OrganizationResponse.from(this.queryService.handle(new GetOrganizationQuery(id))));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<OrganizationResponse> update(
        @PathVariable UUID id,
        @RequestBody @Valid UpdateOrganizationRequest request,
        @RequestHeader("X-Requester-Id") UUID requesterId
    ) {
        this.commandService.handle(request.toCommand(id, requesterId));

        return ResponseEntity.ok(
                OrganizationResponse.from(this.queryService.handle(new GetOrganizationQuery(id))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        this.commandService.handle(new DeleteOrganizationCommand(id));
        return ResponseEntity.noContent().build();
    }
}
