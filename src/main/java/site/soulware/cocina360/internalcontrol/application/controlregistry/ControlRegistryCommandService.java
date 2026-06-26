package site.soulware.cocina360.internalcontrol.application.controlregistry;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.internalcontrol.domain.model.aggregate.ControlFormat;
import site.soulware.cocina360.internalcontrol.domain.model.aggregate.ControlRegistry;
import site.soulware.cocina360.internalcontrol.domain.model.command.CreateControlRegistryCommand;
import site.soulware.cocina360.internalcontrol.domain.model.exception.ControlFormatNotFoundException;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlFormatId;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlRegistryId;
import site.soulware.cocina360.internalcontrol.domain.repository.ControlFormatRepository;
import site.soulware.cocina360.internalcontrol.domain.repository.ControlRegistryRepository;

@Service
@Transactional
public class ControlRegistryCommandService {

    private final ControlRegistryRepository repository;
    private final ControlFormatRepository formatRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ControlRegistryCommandService(
        ControlRegistryRepository repository,
        ControlFormatRepository formatRepository,
        ApplicationEventPublisher eventPublisher
    ) {
        this.repository = repository;
        this.formatRepository = formatRepository;
        this.eventPublisher = eventPublisher;
    }

    public ControlRegistryId handle(CreateControlRegistryCommand command) {
        ControlFormat format = this.formatRepository.findById(ControlFormatId.of(command.formatId()))
                .orElseThrow(() -> ControlFormatNotFoundException.byId(command.formatId()));

        // The aggregate enforces the format's rules: format must be ACTIVE (FormatNotActiveException)
        // and every submitted value must satisfy its field's validations (RegistryValidationException).
        ControlRegistry registry = ControlRegistry.create(ControlRegistryId.generate(), format, command.data());

        this.repository.save(registry);
        registry.pullDomainEvents().forEach(this.eventPublisher::publishEvent);
        return registry.getId();
    }
}
