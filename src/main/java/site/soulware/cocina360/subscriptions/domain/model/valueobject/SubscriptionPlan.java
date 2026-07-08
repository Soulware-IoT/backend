package site.soulware.cocina360.subscriptions.domain.model.valueobject;

public enum SubscriptionPlan {

    FREE(3, 0),
    BASIC(10, 1),
    PROFESSIONAL(-1, 2);

    private final int maxIotDevices;
    private final int tier;

    SubscriptionPlan(int maxIotDevices, int tier) {
        this.maxIotDevices = maxIotDevices;
        this.tier = tier;
    }

    public int maxIotDevices() {
        return this.maxIotDevices;
    }

    public boolean isUnlimited() {
        return this.maxIotDevices == -1;
    }

    public boolean isPaid() {
        return this != FREE;
    }

    /** True when this plan sits above {@code other} in the tier ranking (i.e. changing to it is an upgrade). */
    public boolean isHigherThan(SubscriptionPlan other) {
        return this.tier > other.tier;
    }
}
