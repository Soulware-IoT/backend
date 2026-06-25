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
import site.soulware.cocina360.internalcontrol.domain.model.command.RemoveFormatFieldCommand;
import site.soulware.cocina360.internalcontrol.domain.model.command.ResumeControlFormatCommand;
import site.soulware.cocina360.internalcontrol.domain.model.command.SuspendControlFormatCommand;
import site.soulware.cocina360.internalcontrol.domain.model.query.GetControlFormatQuery;
import site.soulware.cocina360.internalcontrol.domain.model.query.GetControlFormatsByProcessQuery;
import site.soulware.cocina360.internalcontrol.domain.model.query.GetControlProcessQuery;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlFormatId;
import site.soulware.cocina360.internalcontrol.interfaces.rest.controlformat.request.AddFormatFieldRequest;
import site.soulware.cocina360.internalcontrol.interfaces.rest.controlformat.request.CreateControlFormatRequest;
import site.soulware.cocina360.internalcontrol.interfaces.rest.controlformat.request.UpdateFormatFieldRequest;
import site.soulware.cocina360.internalcontrol.interfaces.rest.controlformat.response.ControlFormatResponse;

import java.util.List;
import java.util.UUID;

@RestController
public class ControlFormatController {

    private final ControlFormatCommandService commandService;
    private final ControlFormatQueryService queryService;
    private final ControlProcessQueryService processQueryService;

    public ControlFormatController(
        ControlFormatCommandService commandService,
        ControlFormatQueryService queryService,
        ControlProcessQueryService processQueryService
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.processQueryService = processQueryService;
    }

    @PostMapping("/control-processes/{processId}/formats")
    public ResponseEntity<ControlFormatResponse> create(
        @PathVariable UUID processId,
        @RequestBody @Valid CreateControlFormatRequest request
    ) {
        this.requireProcess(processId);
        ControlFormatId id = this.commandService.handle(request.toCommand(processId));
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ControlFormatResponse.from(this.queryService.handle(new GetControlFormatQuery(id.value())))
        );
    }

    @GetMapping("/control-processes/{processId}/formats")
    public ResponseEntity<List<ControlFormatResponse>> listByProcess(@PathVariable UUID processId) {
        this.requireProcess(processId);
        List<ControlFormatResponse> responses = this.queryService
                .handle(new GetControlFormatsByProcessQuery(processId))
                .stream()
                .map(ControlFormatResponse::from)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/formats/{id}")
    public ResponseEntity<ControlFormatResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                ControlFormatResponse.from(this.queryService.handle(new GetControlFormatQuery(id)))
        );
    }

    @PostMapping("/formats/{id}/activate")
    public ResponseEntity<ControlFormatResponse> activate(@PathVariable UUID id) {
        this.queryService.handle(new GetControlFormatQuery(id));
        this.commandService.handle(new ActivateControlFormatCommand(id));
        return ResponseEntity.ok(
                ControlFormatResponse.from(this.queryService.handle(new GetControlFormatQuery(id)))
        );
    }

    @PostMapping("/formats/{id}/suspend")
    public ResponseEntity<ControlFormatResponse> suspend(@PathVariable UUID id) {
        this.queryService.handle(new GetControlFormatQuery(id));
        this.commandService.handle(new SuspendControlFormatCommand(id));
        return ResponseEntity.ok(
                ControlFormatResponse.from(this.queryService.handle(new GetControlFormatQuery(id)))
        );
    }

    @PostMapping("/formats/{id}/resume")
    public ResponseEntity<ControlFormatResponse> resume(@PathVariable UUID id) {
        this.queryService.handle(new GetControlFormatQuery(id));
        this.commandService.handle(new ResumeControlFormatCommand(id));
        return ResponseEntity.ok(
                ControlFormatResponse.from(this.queryService.handle(new GetControlFormatQuery(id)))
        );
    }

    @PostMapping("/formats/{id}/cease")
    public ResponseEntity<ControlFormatResponse> cease(@PathVariable UUID id) {
        this.queryService.handle(new GetControlFormatQuery(id));
        this.commandService.handle(new CeaseControlFormatCommand(id));
        return ResponseEntity.ok(
                ControlFormatResponse.from(this.queryService.handle(new GetControlFormatQuery(id)))
        );
    }

    @PostMapping("/formats/{id}/fields")
    public ResponseEntity<ControlFormatResponse> addField(
        @PathVariable UUID id,
        @RequestBody @Valid AddFormatFieldRequest request
    ) {
        this.queryService.handle(new GetControlFormatQuery(id));
        this.commandService.handle(request.toCommand(id));
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ControlFormatResponse.from(this.queryService.handle(new GetControlFormatQuery(id)))
        );
    }

    @PatchMapping("/formats/{id}/fields/{fieldId}")
    public ResponseEntity<ControlFormatResponse> updateField(
        @PathVariable UUID id,
        @PathVariable UUID fieldId,
        @RequestBody @Valid UpdateFormatFieldRequest request
    ) {
        this.queryService.handle(new GetControlFormatQuery(id));
        this.commandService.handle(request.toCommand(id, fieldId));
        return ResponseEntity.ok(
                ControlFormatResponse.from(this.queryService.handle(new GetControlFormatQuery(id)))
        );
    }

    @DeleteMapping("/formats/{id}/fields/{fieldId}")
    public ResponseEntity<ControlFormatResponse> removeField(
        @PathVariable UUID id,
        @PathVariable UUID fieldId
    ) {
        this.queryService.handle(new GetControlFormatQuery(id));
        this.commandService.handle(new RemoveFormatFieldCommand(id, fieldId));
        return ResponseEntity.ok(
                ControlFormatResponse.from(this.queryService.handle(new GetControlFormatQuery(id)))
        );
    }

    private void requireProcess(UUID processId) {
        this.processQueryService.handle(new GetControlProcessQuery(processId));
    }
}
