package site.soulware.cocina360.shared.domain.model.exception;

/**
 * Thrown when a required aggregate or entity cannot be found by its identity.
 * Translates to HTTP 404 at the presentation layer.
 */
public class EntityNotFoundException extends DomainException {

    public EntityNotFoundException(Class<?> entityType, Object id) {
        super("%s with id '%s' not found".formatted(entityType.getSimpleName(), id));
    }
}
