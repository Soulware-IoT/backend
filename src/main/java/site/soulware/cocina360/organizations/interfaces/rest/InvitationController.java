package site.soulware.cocina360.organizations.interfaces.rest;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.soulware.cocina360.organizations.application.InvitationCommandService;
import site.soulware.cocina360.organizations.application.InvitationQueryService;
import site.soulware.cocina360.organizations.domain.model.command.AcceptInvitationCommand;
import site.soulware.cocina360.organizations.domain.model.command.DeclineInvitationCommand;
import site.soulware.cocina360.organizations.domain.model.query.GetInvitationQuery;
import site.soulware.cocina360.organizations.domain.model.query.ListOrganizationInvitationsQuery;
import site.soulware.cocina360.organizations.interfaces.rest.request.InviteRequest;
import site.soulware.cocina360.organizations.interfaces.rest.response.InvitationResponse;

import java.util.List;
import java.util.UUID;

@RestController
public class InvitationController {

    private final InvitationCommandService commandService;
    private final InvitationQueryService queryService;

    public InvitationController(
        InvitationCommandService commandService,
        InvitationQueryService queryService
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping("/organizations/{organizationId}/invitations")
    public ResponseEntity<Void> invite(
        @PathVariable UUID organizationId,
        @RequestBody @Valid InviteRequest request,
        @RequestHeader("X-Requester-Id") UUID requesterId
    ) {

        this.commandService.handle(request.toCommand(organizationId, requesterId));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/organizations/{organizationId}/invitations")
    public ResponseEntity<List<InvitationResponse>> listByOrganization(@PathVariable UUID organizationId) {
        return ResponseEntity.ok(
                this.queryService.handle(new ListOrganizationInvitationsQuery(organizationId))
                        .stream().map(InvitationResponse::from).toList());
    }

    @GetMapping("/invitations/{id}")
    public ResponseEntity<InvitationResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                InvitationResponse.from(this.queryService.handle(new GetInvitationQuery(id))));
    }

    @PostMapping("/invitations/{id}/accept")
    public ResponseEntity<InvitationResponse> accept(
        @PathVariable UUID id,
        @RequestHeader("X-Requester-Id") UUID requesterId
    ) {

        this.commandService.handle(new AcceptInvitationCommand(id, requesterId));

        return ResponseEntity.ok(
                InvitationResponse.from(this.queryService.handle(new GetInvitationQuery(id))));
    }

    @PostMapping("/invitations/{id}/decline")
    public ResponseEntity<InvitationResponse> decline(@PathVariable UUID id) {
        this.commandService.handle(new DeclineInvitationCommand(id));

        return ResponseEntity.ok(
                InvitationResponse.from(this.queryService.handle(new GetInvitationQuery(id))));
    }
}
