package site.soulware.cocina360.subscriptions.domain.model.valueobject;

public enum SubscriptionPlan {

    FREE(3),
    BASIC(10),
    PROFESSIONAL(-1);

    private final int maxIotDevices;

    SubscriptionPlan(int maxIotDevices) {
        this.maxIotDevices = maxIotDevices;
    }

    public int maxIotDevices() {
        return this.maxIotDevices;
    }

    public boolean isUnlimited() {
        return this.maxIotDevices == -1;
    }
}
