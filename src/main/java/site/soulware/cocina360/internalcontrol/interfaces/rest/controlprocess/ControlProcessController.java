package site.soulware.cocina360.internalcontrol.interfaces.rest.controlprocess;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.soulware.cocina360.internalcontrol.application.controlprocess.ControlProcessCommandService;
import site.soulware.cocina360.internalcontrol.application.controlprocess.ControlProcessQueryService;
import site.soulware.cocina360.internalcontrol.domain.model.query.GetControlProcessQuery;
import site.soulware.cocina360.internalcontrol.domain.model.query.GetControlProcessesByOrganizationQuery;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlProcessId;
import site.soulware.cocina360.internalcontrol.interfaces.rest.controlprocess.request.CreateControlProcessRequest;
import site.soulware.cocina360.internalcontrol.interfaces.rest.controlprocess.request.RenameControlProcessRequest;
import site.soulware.cocina360.internalcontrol.interfaces.rest.controlprocess.response.ControlProcessResponse;
import site.soulware.cocina360.organizations.interfaces.acl.OrganizationsApi;

import java.util.List;
import java.util.UUID;

@RestController
public class ControlProcessController {

    private final ControlProcessCommandService commandService;
    private final ControlProcessQueryService queryService;
    private final OrganizationsApi organizationsApi;

    public ControlProcessController(
        ControlProcessCommandService commandService,
        ControlProcessQueryService queryService,
        OrganizationsApi organizationsApi
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.organizationsApi = organizationsApi;
    }

    @PostMapping("/organizations/{organizationId}/control-processes")
    public ResponseEntity<ControlProcessResponse> create(
        @PathVariable UUID organizationId,
        @RequestBody @Valid CreateControlProcessRequest request
    ) {
        this.organizationsApi.requireOrganizationId(organizationId);
        ControlProcessId id = this.commandService.handle(request.toCommand(organizationId));
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ControlProcessResponse.from(this.queryService.handle(new GetControlProcessQuery(id.value())))
        );
    }

    @GetMapping("/organizations/{organizationId}/control-processes")
    public ResponseEntity<List<ControlProcessResponse>> listByOrganization(@PathVariable UUID organizationId) {
        this.organizationsApi.requireOrganizationId(organizationId);
        List<ControlProcessResponse> responses = this.queryService
                .handle(new GetControlProcessesByOrganizationQuery(organizationId))
                .stream()
                .map(ControlProcessResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/control-processes/{id}")
    public ResponseEntity<ControlProcessResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                ControlProcessResponse.from(this.queryService.handle(new GetControlProcessQuery(id)))
        );
    }

    @PatchMapping("/control-processes/{id}")
    public ResponseEntity<ControlProcessResponse> rename(
        @PathVariable UUID id,
        @RequestBody @Valid RenameControlProcessRequest request
    ) {
        this.commandService.handle(request.toCommand(id));
        return ResponseEntity.ok(
                ControlProcessResponse.from(this.queryService.handle(new GetControlProcessQuery(id)))
        );
    }
}
