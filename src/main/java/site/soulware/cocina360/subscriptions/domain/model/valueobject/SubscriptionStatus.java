package site.soulware.cocina360.subscriptions.domain.model.valueobject;

public enum SubscriptionStatus {
    ACTIVE,
    SUSPENDED,
    CANCELLED;

    public boolean canSuspend() {
        return this == ACTIVE;
    }

    public boolean canCancel() {
        return this == ACTIVE || this == SUSPENDED;
    }

    public boolean canReactivate() {
        return this == SUSPENDED;
    }
}
