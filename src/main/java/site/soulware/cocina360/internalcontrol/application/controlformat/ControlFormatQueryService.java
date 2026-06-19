package site.soulware.cocina360.internalcontrol.application.controlformat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.internalcontrol.domain.model.exception.ControlFormatNotFoundException;
import site.soulware.cocina360.internalcontrol.domain.model.query.GetControlFormatQuery;
import site.soulware.cocina360.internalcontrol.domain.model.query.GetControlFormatsByProcessQuery;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlFormatId;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlProcessId;
import site.soulware.cocina360.internalcontrol.domain.repository.ControlFormatRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ControlFormatQueryService {

    private final ControlFormatRepository repository;

    public ControlFormatQueryService(ControlFormatRepository repository) {
        this.repository = repository;
    }

    public ControlFormatResult handle(GetControlFormatQuery query) {
        return this.repository.findById(ControlFormatId.of(query.id()))
                .map(ControlFormatResult::from)
                .orElseThrow(() -> ControlFormatNotFoundException.byId(query.id()));
    }

    public List<ControlFormatResult> handle(GetControlFormatsByProcessQuery query) {
        return this.repository.findAllByProcessId(ControlProcessId.of(query.processId()))
                .stream()
                .map(ControlFormatResult::from)
                .toList();
    }
}
