package site.soulware.cocina360.shared.infrastructure.auth;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Injects the authenticated requester's id (the JWT {@code sub} claim, which equals the
 * {@code ProfileId}) into a controller method parameter of type {@link java.util.UUID}.
 * <p>
 * Replaces the former {@code @RequestHeader("X-Requester-Id")}: the gateway no longer extracts
 * the user id, so it is now read from the verified JWT by {@link CurrentUserArgumentResolver}.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUser {
}
