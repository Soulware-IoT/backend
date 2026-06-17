package site.soulware.cocina360.shared.domain.model.exception;

public class EntityNotFoundException extends DomainException {

    public EntityNotFoundException(String messageKey, Object... messageArgs) {
        super(messageKey, messageArgs);
    }
}
