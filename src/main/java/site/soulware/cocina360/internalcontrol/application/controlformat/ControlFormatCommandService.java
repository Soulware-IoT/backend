package site.soulware.cocina360.internalcontrol.application.controlformat;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.internalcontrol.domain.model.aggregate.ControlFormat;
import site.soulware.cocina360.internalcontrol.domain.model.command.CreateControlFormatCommand;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlFormatId;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlProcessId;
import site.soulware.cocina360.internalcontrol.domain.repository.ControlFormatRepository;

@Service
@Transactional
public class ControlFormatCommandService {

    private final ControlFormatRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public ControlFormatCommandService(
        ControlFormatRepository repository,
        ApplicationEventPublisher eventPublisher
    ) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    public ControlFormatId handle(CreateControlFormatCommand command) {
        ControlFormat format = ControlFormat.create(
                ControlFormatId.generate(),
                ControlProcessId.of(command.processId()),
                command.name()
        );
        this.repository.save(format);
        format.pullDomainEvents().forEach(this.eventPublisher::publishEvent);
        return format.getId();
    }
}
