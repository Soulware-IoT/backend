package site.soulware.cocina360.shared.infrastructure.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * Bridges Bean Validation (Jakarta {@code @NotBlank}, {@code @NotNull}, …) to the application's
 * {@link MessageSource} so constraint-violation messages are resolved from the same i18n bundles
 * ({@code messages*.properties}) as the rest of the app — keeping validation errors translatable
 * and consistent with domain error messages.
 */
@Configuration
public class ValidationConfig {

    /**
     * Replaces Boot's default validator with one whose message interpolation reads from the
     * application {@link MessageSource} instead of Hibernate Validator's built-in
     * {@code ValidationMessages.properties}. The effect: constraint messages resolve standard keys
     * (e.g. {@code jakarta.validation.constraints.NotBlank.message}) from {@code messages.properties}
     * /{@code messages_es.properties}, honoring the request's {@code Accept-Language}. The bean name
     * {@code validator} matches what Boot's MVC auto-configuration looks up, so this instance is the
     * one used to validate {@code @Valid} controller request bodies.
     *
     * @param messageSource the application-wide message source (auto-configured by Boot from the
     *                      {@code messages} bundles); injected so validation shares the same catalog
     * @return a validator factory wired to resolve constraint messages through {@code messageSource}
     */
    @Bean
    public LocalValidatorFactoryBean validator(MessageSource messageSource) {
        LocalValidatorFactoryBean factory = new LocalValidatorFactoryBean();
        factory.setValidationMessageSource(messageSource);
        return factory;
    }
}
