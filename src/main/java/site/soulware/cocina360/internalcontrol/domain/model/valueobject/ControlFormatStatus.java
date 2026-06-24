package site.soulware.cocina360.internalcontrol.domain.model.valueobject;

import com.fasterxml.jackson.annotation.JsonValue;
import site.soulware.cocina360.shared.domain.model.valueobject.TranslatableEnum;

public enum ControlFormatStatus implements TranslatableEnum {
    DRAFT("draft"),
    ACTIVE("active"),
    SUSPENDED("suspended"),
    CEASED("ceased");

    private final String label;

    ControlFormatStatus(String label) {
        this.label = label;
    }

    @JsonValue
    public String label() {
        return this.label;
    }

    @Override
    public String messageKey() {
        return "enum.control.format.status." + this.label;
    }

    public boolean canTransitionTo(ControlFormatStatus target) {
        return switch (this) {
            case DRAFT -> target == ACTIVE;
            case ACTIVE -> target == SUSPENDED || target == CEASED;
            case SUSPENDED -> target == ACTIVE || target == CEASED;
            case CEASED -> false;
        };
    }
}
