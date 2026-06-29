package site.soulware.cocina360.shared.infrastructure.rest;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import site.soulware.cocina360.shared.domain.model.exception.BusinessRuleViolationException;
import site.soulware.cocina360.shared.domain.model.exception.DomainException;
import site.soulware.cocina360.shared.domain.model.exception.EntityNotFoundException;
import site.soulware.cocina360.shared.domain.model.exception.ForbiddenException;
import site.soulware.cocina360.shared.domain.model.exception.UnauthorizedException;
import site.soulware.cocina360.shared.domain.model.exception.ValidationException;
import site.soulware.cocina360.shared.infrastructure.rest.i18n.MessageResolver;
import site.soulware.cocina360.shared.infrastructure.rest.response.ErrorResponse;
import site.soulware.cocina360.shared.infrastructure.rest.response.ValidationErrorResponse;

import java.util.List;

/**
 * Maps {@link DomainException}s to their HTTP responses. Runs at highest precedence so domain
 * exceptions are resolved here before the generic {@link WebExceptionHandler} (whose
 * {@code Exception} catch-all would otherwise also match them).
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class DomainExceptionHandler {

    private final MessageResolver messages;

    public DomainExceptionHandler(MessageResolver messages) {
        this.messages = messages;
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handle(EntityNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.of(404, "Not Found", this.messages.resolve(ex)));
    }

    @ExceptionHandler(BusinessRuleViolationException.class)
    public ResponseEntity<ErrorResponse> handle(BusinessRuleViolationException ex) {
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(ErrorResponse.of(422, "Unprocessable Entity", this.messages.resolve(ex)));
    }

    /**
     * Specialization of the 422 mapping for multi-field validation failures: resolves the summary
     * message and each field's reason, returning a {@link ValidationErrorResponse} that carries the
     * per-field detail. Being a more specific {@code @ExceptionHandler} than the
     * {@link BusinessRuleViolationException} one above, Spring selects it for {@code ValidationException}.
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ValidationErrorResponse> handle(ValidationException ex) {
        List<ValidationErrorResponse.FieldError> violations = ex.getViolations().stream()
                .map(violation -> new ValidationErrorResponse.FieldError(
                        violation.fieldKey(),
                        this.messages.get(violation.messageKey())))
                .toList();
        return ResponseEntity
                .status(HttpStatus.UNPROCESSABLE_CONTENT)
                .body(ValidationErrorResponse.of(this.messages.resolve(ex), violations));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handle(UnauthorizedException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.of(401, "Unauthorized", this.messages.resolve(ex)));
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handle(ForbiddenException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.of(403, "Forbidden", this.messages.resolve(ex)));
    }

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handle(DomainException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(400, "Bad Request", this.messages.resolve(ex)));
    }
}
