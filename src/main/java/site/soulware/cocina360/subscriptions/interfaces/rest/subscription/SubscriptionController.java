package site.soulware.cocina360.subscriptions.interfaces.rest.subscription;

import jakarta.validation.Valid;
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
import site.soulware.cocina360.subscriptions.domain.model.command.DowngradeSubscriptionCommand;
import site.soulware.cocina360.subscriptions.domain.model.command.ResumeSubscriptionCommand;
import site.soulware.cocina360.subscriptions.domain.model.exception.NotSubscriptionOwnerException;
import site.soulware.cocina360.subscriptions.domain.model.query.GetSubscriptionByOrganizationQuery;
import site.soulware.cocina360.subscriptions.domain.model.query.GetSubscriptionInvoicesQuery;
import site.soulware.cocina360.subscriptions.interfaces.rest.subscription.request.ChangeSubscriptionPlanRequest;
import site.soulware.cocina360.subscriptions.interfaces.rest.subscription.response.InvoiceResponse;
import site.soulware.cocina360.subscriptions.interfaces.rest.subscription.response.SubscriptionResponse;

import java.util.List;
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

    @GetMapping
    public ResponseEntity<SubscriptionResponse> getByOrganization(
        @PathVariable UUID organizationId,
        @CurrentUser UUID requesterId
    ) {
        this.organizationsApi.requireOrganizationId(organizationId);
        this.authorizationApi.requirePermission(organizationId, requesterId, PermissionArea.ORGANIZATIONS, AccessLevel.LIEUTENANT);

        return ResponseEntity.ok(
                SubscriptionResponse.from(this.queryService.handleWithBilling(new GetSubscriptionByOrganizationQuery(organizationId))));
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
                SubscriptionResponse.from(this.queryService.handleWithBilling(new GetSubscriptionByOrganizationQuery(organizationId))));
    }

    /** Schedules a downgrade to FREE at the end of the current paid period (Stripe cancel_at_period_end). */
    @PostMapping("/downgrade")
    public ResponseEntity<SubscriptionResponse> downgrade(
        @PathVariable UUID organizationId,
        @CurrentUser UUID requesterId
    ) {
        this.requireOwner(organizationId, requesterId);
        this.commandService.handle(new DowngradeSubscriptionCommand(organizationId, requesterId));

        return ResponseEntity.ok(
                SubscriptionResponse.from(this.queryService.handleWithBilling(new GetSubscriptionByOrganizationQuery(organizationId))));
    }

    /** Cancels a pending end-of-period downgrade — the subscription keeps renewing. */
    @PostMapping("/resume")
    public ResponseEntity<SubscriptionResponse> resume(
        @PathVariable UUID organizationId,
        @CurrentUser UUID requesterId
    ) {
        this.requireOwner(organizationId, requesterId);
        this.commandService.handle(new ResumeSubscriptionCommand(organizationId, requesterId));

        return ResponseEntity.ok(
                SubscriptionResponse.from(this.queryService.handleWithBilling(new GetSubscriptionByOrganizationQuery(organizationId))));
    }

    /** Owner-only: invoices expose billed amounts, so gate them like the billing mutations. */
    @GetMapping("/invoices")
    public ResponseEntity<List<InvoiceResponse>> listInvoices(
        @PathVariable UUID organizationId,
        @CurrentUser UUID requesterId
    ) {
        this.requireOwner(organizationId, requesterId);

        return ResponseEntity.ok(
                InvoiceResponse.fromAll(this.queryService.listInvoices(new GetSubscriptionInvoicesQuery(organizationId))));
    }

    private void requireOwner(UUID organizationId, UUID requesterId) {
        ProfileId ownerId = this.organizationsApi.requireOwnerProfileId(organizationId);
        if (!ownerId.equals(ProfileId.of(requesterId))) {
            throw new NotSubscriptionOwnerException();
        }
    }
}
