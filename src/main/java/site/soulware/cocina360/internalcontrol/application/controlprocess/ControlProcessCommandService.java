package site.soulware.cocina360.internalcontrol.application.controlprocess;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.internalcontrol.domain.model.aggregate.ControlProcess;
import site.soulware.cocina360.internalcontrol.domain.model.command.CreateControlProcessCommand;
import site.soulware.cocina360.internalcontrol.domain.model.command.RenameControlProcessCommand;
import site.soulware.cocina360.internalcontrol.domain.model.exception.ControlProcessNotFoundException;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlProcessId;
import site.soulware.cocina360.internalcontrol.domain.repository.ControlProcessRepository;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;

@Service
@Transactional
public class ControlProcessCommandService {

    private final ControlProcessRepository repository;
    private final ApplicationEventPublisher eventPublisher;

    public ControlProcessCommandService(
        ControlProcessRepository repository,
        ApplicationEventPublisher eventPublisher
    ) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    public ControlProcessId handle(CreateControlProcessCommand command) {
        ControlProcess process = ControlProcess.create(
                ControlProcessId.generate(),
                OrganizationId.of(command.organizationId()),
                command.name()
        );
        this.repository.save(process);
        process.pullDomainEvents().forEach(this.eventPublisher::publishEvent);
        return process.getId();
    }

    public void handle(RenameControlProcessCommand command) {
        ControlProcess process = this.findOrThrow(ControlProcessId.of(command.id()));
        process.rename(command.name());
        this.repository.save(process);
        process.pullDomainEvents().forEach(this.eventPublisher::publishEvent);
    }

    private ControlProcess findOrThrow(ControlProcessId id) {
        return this.repository.findById(id)
                .orElseThrow(() -> ControlProcessNotFoundException.byId(id.value()));
    }
}
