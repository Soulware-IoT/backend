package site.soulware.cocina360.internalcontrol.interfaces.rest.controlregistry;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import site.soulware.cocina360.internalcontrol.application.controlformat.ControlFormatQueryService;
import site.soulware.cocina360.internalcontrol.application.controlregistry.ControlRegistryCommandService;
import site.soulware.cocina360.internalcontrol.application.controlregistry.ControlRegistryQueryService;
import site.soulware.cocina360.internalcontrol.domain.model.query.GetControlFormatQuery;
import site.soulware.cocina360.internalcontrol.domain.model.query.GetControlRegistriesByFormatQuery;
import site.soulware.cocina360.internalcontrol.domain.model.query.GetControlRegistryQuery;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlRegistryId;
import site.soulware.cocina360.internalcontrol.infrastructure.persistence.authz.ControlOrganizationQuery;
import site.soulware.cocina360.internalcontrol.interfaces.rest.controlregistry.request.CreateControlRegistryRequest;
import site.soulware.cocina360.internalcontrol.interfaces.rest.controlregistry.response.ControlRegistryResponse;
import site.soulware.cocina360.organizations.interfaces.acl.AccessLevel;
import site.soulware.cocina360.organizations.interfaces.acl.AuthorizationApi;
import site.soulware.cocina360.organizations.interfaces.acl.PermissionArea;
import site.soulware.cocina360.shared.infrastructure.auth.CurrentUser;

import java.util.List;
import java.util.UUID;

@RestController
public class ControlRegistryController {

    private final ControlRegistryCommandService commandService;
    private final ControlRegistryQueryService queryService;
    private final ControlFormatQueryService formatQueryService;
    private final AuthorizationApi authorizationApi;
    private final ControlOrganizationQuery controlOrganizationQuery;

    public ControlRegistryController(
        ControlRegistryCommandService commandService,
        ControlRegistryQueryService queryService,
        ControlFormatQueryService formatQueryService,
        AuthorizationApi authorizationApi,
        ControlOrganizationQuery controlOrganizationQuery
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.formatQueryService = formatQueryService;
        this.authorizationApi = authorizationApi;
        this.controlOrganizationQuery = controlOrganizationQuery;
    }

    @PostMapping("/formats/{formatId}/registries")
    public ResponseEntity<ControlRegistryResponse> create(
        @PathVariable UUID formatId,
        @RequestBody @Valid CreateControlRegistryRequest request,
        @CurrentUser UUID requesterId
    ) {
        this.requireFormat(formatId);
        this.authorizeByFormat(formatId, requesterId, AccessLevel.ASSIGNEE);
        ControlRegistryId id = this.commandService.handle(request.toCommand(formatId));
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ControlRegistryResponse.from(this.queryService.handle(new GetControlRegistryQuery(id.value())))
        );
    }

    @GetMapping("/formats/{formatId}/registries")
    public ResponseEntity<List<ControlRegistryResponse>> listByFormat(
        @PathVariable UUID formatId,
        @CurrentUser UUID requesterId
    ) {
        this.requireFormat(formatId);
        this.authorizeByFormat(formatId, requesterId, AccessLevel.LIEUTENANT);
        List<ControlRegistryResponse> responses = this.queryService
                .handle(new GetControlRegistriesByFormatQuery(formatId))
                .stream()
                .map(ControlRegistryResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/registries/{id}")
    public ResponseEntity<ControlRegistryResponse> getById(
        @PathVariable UUID id,
        @CurrentUser UUID requesterId
    ) {
        this.controlOrganizationQuery.findByRegistry(id)
                .ifPresent(orgId -> this.authorizationApi.requirePermission(orgId, requesterId, PermissionArea.INTERNAL_CONTROL, AccessLevel.LIEUTENANT));
        return ResponseEntity.ok(
                ControlRegistryResponse.from(this.queryService.handle(new GetControlRegistryQuery(id)))
        );
    }

    private void requireFormat(UUID formatId) {
        this.formatQueryService.handle(new GetControlFormatQuery(formatId));
    }

    private void authorizeByFormat(UUID formatId, UUID requesterId, AccessLevel minimum) {
        this.controlOrganizationQuery.findByFormat(formatId)
                .ifPresent(orgId -> this.authorizationApi.requirePermission(orgId, requesterId, PermissionArea.INTERNAL_CONTROL, minimum));
    }
}
