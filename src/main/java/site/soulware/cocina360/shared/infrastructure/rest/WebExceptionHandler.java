package site.soulware.cocina360.shared.infrastructure.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import site.soulware.cocina360.shared.infrastructure.rest.i18n.MessageResolver;
import site.soulware.cocina360.shared.infrastructure.rest.response.ErrorResponse;

import java.util.stream.Collectors;

/**
 * Maps framework/Java exceptions (Bean Validation failures, type mismatches) and any otherwise
 * unhandled exception to an {@link ErrorResponse}. Runs at lowest precedence so its
 * {@code Exception} catch-all only applies after the more specific {@link DomainExceptionHandler}.
 */
@Order(Ordered.LOWEST_PRECEDENCE)
@RestControllerAdvice
public class WebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(WebExceptionHandler.class);

    private final MessageResolver messages;

    public WebExceptionHandler(MessageResolver messages) {
        this.messages = messages;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handle(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining(", "));
        String error = this.messages.get("error.validation.failed");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(400, error, message));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handle(MethodArgumentTypeMismatchException ex) {
        String message = this.messages.get("error.type_mismatch", ex.getValue(), ex.getName());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.of(400, "Bad Request", message));
    }

    /**
     * A streaming client (e.g. an SSE subscriber) went away mid-response: the response is
     * committed and unusable, so there is nothing to write and nobody to write it to.
     * Handled quietly — without this, the {@code Exception} catch-all would try to write
     * a JSON error body into the dead event-stream response and fail loudly on every
     * disconnect.
     */
    @ExceptionHandler(AsyncRequestNotUsableException.class)
    public void handle(AsyncRequestNotUsableException ex) {
        log.debug("Async response no longer usable (client disconnected): {}", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handle(Exception ex) {
        String message = this.messages.get("error.internal");
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(500, "Internal Server Error", message));
    }
}
