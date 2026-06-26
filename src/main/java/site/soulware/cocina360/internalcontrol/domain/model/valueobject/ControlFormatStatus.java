package site.soulware.cocina360.internalcontrol.domain.model.valueobject;

public enum ControlFormatStatus {
    DRAFT,
    ACTIVE,
    SUSPENDED,
    CEASED;

    public boolean canTransitionTo(ControlFormatStatus target) {
        return switch (this) {
            case DRAFT -> target == ACTIVE;
            case ACTIVE -> target == SUSPENDED || target == CEASED;
            case SUSPENDED -> target == ACTIVE || target == CEASED;
            case CEASED -> false;
        };
    }
}
