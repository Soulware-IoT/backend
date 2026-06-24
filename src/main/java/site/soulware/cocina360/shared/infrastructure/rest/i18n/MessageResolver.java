package site.soulware.cocina360.shared.infrastructure.rest.i18n;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;
import site.soulware.cocina360.shared.domain.model.exception.DomainException;
import site.soulware.cocina360.shared.domain.model.valueobject.TranslatableEnum;

/**
 * Resolves i18n messages against the application {@code MessageSource} using the locale of
 * the current request ({@code Accept-Language}). Shared by the REST exception handlers so the
 * message-resolution logic lives in one place.
 */
@Component
public class MessageResolver {

    private final MessageSource messageSource;

    public MessageResolver(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /** Resolves a domain exception's own {@code messageKey} + {@code messageArgs}. */
    public String resolve(DomainException ex) {
        return this.get(ex.getMessageKey(), ex.getMessageArgs());
    }

    /** Resolves an arbitrary message key with optional positional arguments. */
    public String get(String key, Object... args) {
        return this.messageSource.getMessage(key, this.localize(args), LocaleContextHolder.getLocale());
    }

    /**
     * Replaces any {@link TranslatableEnum} argument with its locale-resolved label, so enum
     * values surface translated (not their internal name) inside the formatted message.
     */
    private Object[] localize(Object[] args) {
        if (args == null) {
            return null;
        }
        Object[] localized = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            localized[i] = args[i] instanceof TranslatableEnum e ? this.get(e.messageKey()) : args[i];
        }
        return localized;
    }
}
