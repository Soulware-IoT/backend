package site.soulware.cocina360.subscriptions.domain.model.exception;

import site.soulware.cocina360.shared.domain.model.exception.BusinessRuleViolationException;

public class InvoiceRetrievalFailedException extends BusinessRuleViolationException {

    public InvoiceRetrievalFailedException(String reason) {
        super("error.subscription.invoice_retrieval_failed", reason);
    }
}
