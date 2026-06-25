package site.soulware.cocina360.internalcontrol.application.controlformat;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.internalcontrol.domain.model.aggregate.ControlFormat;
import site.soulware.cocina360.internalcontrol.domain.model.command.ActivateControlFormatCommand;
import site.soulware.cocina360.internalcontrol.domain.model.command.AddFormatFieldCommand;
import site.soulware.cocina360.internalcontrol.domain.model.command.CeaseControlFormatCommand;
import site.soulware.cocina360.internalcontrol.domain.model.command.CreateControlFormatCommand;
import site.soulware.cocina360.internalcontrol.domain.model.command.RemoveFormatFieldCommand;
import site.soulware.cocina360.internalcontrol.domain.model.command.ResumeControlFormatCommand;
import site.soulware.cocina360.internalcontrol.domain.model.command.SuspendControlFormatCommand;
import site.soulware.cocina360.internalcontrol.domain.model.command.UpdateFormatFieldCommand;
import site.soulware.cocina360.internalcontrol.domain.model.entity.FormatField;
import site.soulware.cocina360.internalcontrol.domain.model.exception.ControlFormatNotFoundException;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlFormatId;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlProcessId;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.FormatFieldId;
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
        ControlFormatId id = ControlFormatId.generate();
        ControlProcessId processId = ControlProcessId.of(command.processId());

        ControlFormat format = command.createSampleFields()
                ? ControlFormat.createWithSampleFields(id, processId, command.name())
                : ControlFormat.create(id, processId, command.name());
        this.repository.save(format);
        format.pullDomainEvents().forEach(this.eventPublisher::publishEvent);
        return format.getId();
    }

    public void handle(ActivateControlFormatCommand command) {
        ControlFormat format = this.findOrThrow(ControlFormatId.of(command.id()));
        format.activate();
        this.persist(format);
    }

    public void handle(SuspendControlFormatCommand command) {
        ControlFormat format = this.findOrThrow(ControlFormatId.of(command.id()));
        format.suspend();
        this.persist(format);
    }

    public void handle(ResumeControlFormatCommand command) {
        ControlFormat format = this.findOrThrow(ControlFormatId.of(command.id()));
        format.resume();
        this.persist(format);
    }

    public void handle(CeaseControlFormatCommand command) {
        ControlFormat format = this.findOrThrow(ControlFormatId.of(command.id()));
        format.cease();
        this.persist(format);
    }

    public void handle(AddFormatFieldCommand command) {
        ControlFormat format = this.findOrThrow(ControlFormatId.of(command.formatId()));
        format.addField(FormatField.create(
                FormatFieldId.generate(),
                command.key(),
                command.label(),
                command.type(),
                command.required(),
                command.displayOrder(),
                command.validationRules()
        ));
        this.persist(format);
    }

    public void handle(UpdateFormatFieldCommand command) {
        ControlFormat format = this.findOrThrow(ControlFormatId.of(command.formatId()));
        format.updateField(
                FormatFieldId.of(command.fieldId()),
                command.label(),
                command.required(),
                command.displayOrder(),
                command.validationRules()
        );
        this.persist(format);
    }

    public void handle(RemoveFormatFieldCommand command) {
        ControlFormat format = this.findOrThrow(ControlFormatId.of(command.formatId()));
        format.removeField(FormatFieldId.of(command.fieldId()));
        this.persist(format);
    }

    private ControlFormat findOrThrow(ControlFormatId id) {
        return this.repository.findById(id)
                .orElseThrow(() -> ControlFormatNotFoundException.byId(id.value()));
    }

    private void persist(ControlFormat format) {
        this.repository.save(format);
        format.pullDomainEvents().forEach(this.eventPublisher::publishEvent);
    }
}
