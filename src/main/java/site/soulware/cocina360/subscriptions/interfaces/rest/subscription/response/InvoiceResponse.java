package site.soulware.cocina360.subscriptions.interfaces.rest.subscription.response;

import site.soulware.cocina360.subscriptions.application.subscription.BillingGateway;

import java.time.Instant;
import java.util.List;

public record InvoiceResponse(
        String number,
        String status,
        long amountPaid,
        String currency,
        Instant createdAt,
        String hostedInvoiceUrl,
        String invoicePdfUrl
) {
    public static InvoiceResponse from(BillingGateway.InvoiceView view) {
        return new InvoiceResponse(
                view.number(),
                view.status(),
                view.amountPaid(),
                view.currency(),
                view.createdAt(),
                view.hostedInvoiceUrl(),
                view.invoicePdfUrl()
        );
    }

    public static List<InvoiceResponse> fromAll(List<BillingGateway.InvoiceView> views) {
        return views.stream().map(InvoiceResponse::from).toList();
    }
}
