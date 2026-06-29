package site.soulware.cocina360.internalcontrol.interfaces.rest.controlformat;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.soulware.cocina360.internalcontrol.application.controlformat.ControlFormatCommandService;
import site.soulware.cocina360.internalcontrol.application.controlformat.ControlFormatQueryService;
import site.soulware.cocina360.internalcontrol.application.controlprocess.ControlProcessQueryService;
import site.soulware.cocina360.internalcontrol.domain.model.command.ActivateControlFormatCommand;
import site.soulware.cocina360.internalcontrol.domain.model.command.CeaseControlFormatCommand;
import site.soulware.cocina360.internalcontrol.domain.model.command.ResumeControlFormatCommand;
import site.soulware.cocina360.internalcontrol.domain.model.command.SuspendControlFormatCommand;
import site.soulware.cocina360.internalcontrol.domain.model.query.GetControlFormatQuery;
import site.soulware.cocina360.internalcontrol.domain.model.query.GetControlFormatsByProcessQuery;
import site.soulware.cocina360.internalcontrol.domain.model.query.GetControlProcessQuery;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlFormatId;
import site.soulware.cocina360.internalcontrol.infrastructure.persistence.authz.ControlOrganizationQuery;
import site.soulware.cocina360.internalcontrol.interfaces.rest.controlformat.request.CreateControlFormatRequest;
import site.soulware.cocina360.internalcontrol.interfaces.rest.controlformat.request.ReplaceFormatFieldsRequest;
import site.soulware.cocina360.internalcontrol.interfaces.rest.controlformat.response.ControlFormatResponse;
import site.soulware.cocina360.organizations.interfaces.acl.AccessLevel;
import site.soulware.cocina360.organizations.interfaces.acl.AuthorizationApi;
import site.soulware.cocina360.organizations.interfaces.acl.PermissionArea;
import site.soulware.cocina360.shared.infrastructure.auth.CurrentUser;

import java.util.List;
import java.util.UUID;

@RestController
public class ControlFormatController {

    private final ControlFormatCommandService commandService;
    private final ControlFormatQueryService queryService;
    private final ControlProcessQueryService processQueryService;
    private final AuthorizationApi authorizationApi;
    private final ControlOrganizationQuery controlOrganizationQuery;

    public ControlFormatController(
        ControlFormatCommandService commandService,
        ControlFormatQueryService queryService,
        ControlProcessQueryService processQueryService,
        AuthorizationApi authorizationApi,
        ControlOrganizationQuery controlOrganizationQuery
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.processQueryService = processQueryService;
        this.authorizationApi = authorizationApi;
        this.controlOrganizationQuery = controlOrganizationQuery;
    }

    @PostMapping("/control-processes/{processId}/formats")
    public ResponseEntity<ControlFormatResponse> create(
        @PathVariable UUID processId,
        @RequestBody @Valid CreateControlFormatRequest request,
        @CurrentUser UUID requesterId
    ) {
        this.requireProcess(processId);
        this.authorizeByProcess(processId, requesterId, AccessLevel.ADMIN);
        ControlFormatId id = this.commandService.handle(request.toCommand(processId));
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ControlFormatResponse.from(this.queryService.handle(new GetControlFormatQuery(id.value())))
        );
    }

    @GetMapping("/control-processes/{processId}/formats")
    public ResponseEntity<List<ControlFormatResponse>> listByProcess(
        @PathVariable UUID processId,
        @CurrentUser UUID requesterId
    ) {
        this.requireProcess(processId);
        this.authorizeByProcess(processId, requesterId, AccessLevel.ASSIGNEE);
        List<ControlFormatResponse> responses = this.queryService
                .handle(new GetControlFormatsByProcessQuery(processId))
                .stream()
                .map(ControlFormatResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/formats/{id}")
    public ResponseEntity<ControlFormatResponse> getById(
        @PathVariable UUID id,
        @CurrentUser UUID requesterId
    ) {
        this.authorizeByFormat(id, requesterId, AccessLevel.ASSIGNEE);
        return ResponseEntity.ok(
                ControlFormatResponse.from(this.queryService.handle(new GetControlFormatQuery(id)))
        );
    }

    @PostMapping("/formats/{id}/activate")
    public ResponseEntity<ControlFormatResponse> activate(@PathVariable UUID id, @CurrentUser UUID requesterId) {
        this.authorizeByFormat(id, requesterId, AccessLevel.ADMIN);
        this.queryService.handle(new GetControlFormatQuery(id));
        this.commandService.handle(new ActivateControlFormatCommand(id));
        return ResponseEntity.ok(
                ControlFormatResponse.from(this.queryService.handle(new GetControlFormatQuery(id)))
        );
    }

    @PostMapping("/formats/{id}/suspend")
    public ResponseEntity<ControlFormatResponse> suspend(@PathVariable UUID id, @CurrentUser UUID requesterId) {
        this.authorizeByFormat(id, requesterId, AccessLevel.ADMIN);
        this.queryService.handle(new GetControlFormatQuery(id));
        this.commandService.handle(new SuspendControlFormatCommand(id));
        return ResponseEntity.ok(
                ControlFormatResponse.from(this.queryService.handle(new GetControlFormatQuery(id)))
        );
    }

    @PostMapping("/formats/{id}/resume")
    public ResponseEntity<ControlFormatResponse> resume(@PathVariable UUID id, @CurrentUser UUID requesterId) {
        this.authorizeByFormat(id, requesterId, AccessLevel.ADMIN);
        this.queryService.handle(new GetControlFormatQuery(id));
        this.commandService.handle(new ResumeControlFormatCommand(id));
        return ResponseEntity.ok(
                ControlFormatResponse.from(this.queryService.handle(new GetControlFormatQuery(id)))
        );
    }

    @PostMapping("/formats/{id}/cease")
    public ResponseEntity<ControlFormatResponse> cease(@PathVariable UUID id, @CurrentUser UUID requesterId) {
        this.authorizeByFormat(id, requesterId, AccessLevel.ADMIN);
        this.queryService.handle(new GetControlFormatQuery(id));
        this.commandService.handle(new CeaseControlFormatCommand(id));
        return ResponseEntity.ok(
                ControlFormatResponse.from(this.queryService.handle(new GetControlFormatQuery(id)))
        );
    }

    @PutMapping("/formats/{id}/fields")
    public ResponseEntity<ControlFormatResponse> replaceFields(
        @PathVariable UUID id,
        @RequestBody @Valid ReplaceFormatFieldsRequest request,
        @CurrentUser UUID requesterId
    ) {
        this.authorizeByFormat(id, requesterId, AccessLevel.ADMIN);
        this.queryService.handle(new GetControlFormatQuery(id));
        this.commandService.handle(request.toCommand(id));
        return ResponseEntity.ok(
                ControlFormatResponse.from(this.queryService.handle(new GetControlFormatQuery(id)))
        );
    }

    private void requireProcess(UUID processId) {
        this.processQueryService.handle(new GetControlProcessQuery(processId));
    }

    private void authorizeByProcess(UUID processId, UUID requesterId, AccessLevel minimum) {
        this.controlOrganizationQuery.findByProcess(processId)
                .ifPresent(orgId -> this.authorizationApi.requirePermission(orgId, requesterId, PermissionArea.INTERNAL_CONTROL, minimum));
    }

    private void authorizeByFormat(UUID formatId, UUID requesterId, AccessLevel minimum) {
        this.controlOrganizationQuery.findByFormat(formatId)
                .ifPresent(orgId -> this.authorizationApi.requirePermission(orgId, requesterId, PermissionArea.INTERNAL_CONTROL, minimum));
    }
}
