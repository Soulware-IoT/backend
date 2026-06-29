package site.soulware.cocina360.subscriptions.interfaces.rest.subscription;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.soulware.cocina360.organizations.interfaces.acl.AccessLevel;
import site.soulware.cocina360.organizations.interfaces.acl.AuthorizationApi;
import site.soulware.cocina360.organizations.interfaces.acl.OrganizationsApi;
import site.soulware.cocina360.organizations.interfaces.acl.PermissionArea;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;
import site.soulware.cocina360.shared.infrastructure.auth.CurrentUser;
import site.soulware.cocina360.subscriptions.application.subscription.SubscriptionCommandService;
import site.soulware.cocina360.subscriptions.application.subscription.SubscriptionQueryService;
import site.soulware.cocina360.subscriptions.domain.model.command.CancelSubscriptionCommand;
import site.soulware.cocina360.subscriptions.domain.model.command.ReactivateSubscriptionCommand;
import site.soulware.cocina360.subscriptions.domain.model.command.SuspendSubscriptionCommand;
import site.soulware.cocina360.subscriptions.domain.model.exception.NotSubscriptionOwnerException;
import site.soulware.cocina360.subscriptions.domain.model.query.GetSubscriptionByOrganizationQuery;
import site.soulware.cocina360.subscriptions.interfaces.rest.subscription.request.ChangeSubscriptionPlanRequest;
import site.soulware.cocina360.subscriptions.interfaces.rest.subscription.request.CreateSubscriptionRequest;
import site.soulware.cocina360.subscriptions.interfaces.rest.subscription.response.SubscriptionResponse;

import java.util.UUID;

@RestController
@RequestMapping("/organizations/{organizationId}/subscription")
public class SubscriptionController {

    private final SubscriptionCommandService commandService;
    private final SubscriptionQueryService queryService;
    private final OrganizationsApi organizationsApi;
    private final AuthorizationApi authorizationApi;

    public SubscriptionController(
        SubscriptionCommandService commandService,
        SubscriptionQueryService queryService,
        OrganizationsApi organizationsApi,
        AuthorizationApi authorizationApi
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.organizationsApi = organizationsApi;
        this.authorizationApi = authorizationApi;
    }

    @PostMapping
    public ResponseEntity<SubscriptionResponse> create(
        @PathVariable UUID organizationId,
        @RequestBody @Valid CreateSubscriptionRequest request,
        @CurrentUser UUID requesterId
    ) {
        this.organizationsApi.requireOrganizationId(organizationId);
        this.authorizationApi.requirePermission(organizationId, requesterId, PermissionArea.ORGANIZATIONS, AccessLevel.ADMIN);
        this.commandService.handle(request.toCommand(organizationId, requesterId));

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SubscriptionResponse.from(this.queryService.handle(new GetSubscriptionByOrganizationQuery(organizationId))));
    }

    @GetMapping
    public ResponseEntity<SubscriptionResponse> getByOrganization(
        @PathVariable UUID organizationId,
        @CurrentUser UUID requesterId
    ) {
        this.organizationsApi.requireOrganizationId(organizationId);
        this.authorizationApi.requirePermission(organizationId, requesterId, PermissionArea.ORGANIZATIONS, AccessLevel.LIEUTENANT);

        return ResponseEntity.ok(
                SubscriptionResponse.from(this.queryService.handle(new GetSubscriptionByOrganizationQuery(organizationId))));
    }

    @PostMapping("/plan")
    public ResponseEntity<SubscriptionResponse> changePlan(
        @PathVariable UUID organizationId,
        @RequestBody @Valid ChangeSubscriptionPlanRequest request,
        @CurrentUser UUID requesterId
    ) {
        this.requireOwner(organizationId, requesterId);
        this.commandService.handle(request.toCommand(organizationId, requesterId));

        return ResponseEntity.ok(
                SubscriptionResponse.from(this.queryService.handle(new GetSubscriptionByOrganizationQuery(organizationId))));
    }

    @PostMapping("/suspend")
    public ResponseEntity<SubscriptionResponse> suspend(
        @PathVariable UUID organizationId,
        @CurrentUser UUID requesterId
    ) {
        this.requireOwner(organizationId, requesterId);
        this.commandService.handle(new SuspendSubscriptionCommand(organizationId, requesterId));

        return ResponseEntity.ok(
                SubscriptionResponse.from(this.queryService.handle(new GetSubscriptionByOrganizationQuery(organizationId))));
    }

    @PostMapping("/cancel")
    public ResponseEntity<SubscriptionResponse> cancel(
        @PathVariable UUID organizationId,
        @CurrentUser UUID requesterId
    ) {
        this.requireOwner(organizationId, requesterId);
        this.commandService.handle(new CancelSubscriptionCommand(organizationId, requesterId));

        return ResponseEntity.ok(
                SubscriptionResponse.from(this.queryService.handle(new GetSubscriptionByOrganizationQuery(organizationId))));
    }

    @PostMapping("/reactivate")
    public ResponseEntity<SubscriptionResponse> reactivate(
        @PathVariable UUID organizationId,
        @CurrentUser UUID requesterId
    ) {
        this.requireOwner(organizationId, requesterId);
        this.commandService.handle(new ReactivateSubscriptionCommand(organizationId, requesterId));

        return ResponseEntity.ok(
                SubscriptionResponse.from(this.queryService.handle(new GetSubscriptionByOrganizationQuery(organizationId))));
    }

    private void requireOwner(UUID organizationId, UUID requesterId) {
        ProfileId ownerId = this.organizationsApi.requireOwnerProfileId(organizationId);
        if (!ownerId.equals(ProfileId.of(requesterId))) {
            throw new NotSubscriptionOwnerException();
        }
    }
}
