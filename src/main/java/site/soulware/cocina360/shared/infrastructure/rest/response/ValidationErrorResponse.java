package site.soulware.cocina360.shared.infrastructure.rest.response;

import java.time.Instant;
import java.util.List;

/**
 * Error envelope for a {@code ValidationException} (HTTP 422). Mirrors {@link ErrorResponse} but
 * adds a {@code violations} list, so a multi-field validation failure tells the client exactly which
 * submitted field failed and why — the resolved per-field reasons make the error actionable.
 */
public record ValidationErrorResponse(
        int status,
        String error,
        String message,
        List<FieldError> violations,
        Instant timestamp
) {

    /** One rejected field: its key (e.g. a form field key) and the resolved, translated reason. */
    public record FieldError(String field, String message) {}

    public static ValidationErrorResponse of(String message, List<FieldError> violations) {
        return new ValidationErrorResponse(422, "Unprocessable Entity", message, violations, Instant.now());
    }
}
