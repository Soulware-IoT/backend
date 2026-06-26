package site.soulware.cocina360.internalcontrol.application.controlregistry;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.internalcontrol.domain.model.exception.ControlRegistryNotFoundException;
import site.soulware.cocina360.internalcontrol.domain.model.query.GetControlRegistriesByFormatQuery;
import site.soulware.cocina360.internalcontrol.domain.model.query.GetControlRegistryQuery;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlFormatId;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlRegistryId;
import site.soulware.cocina360.internalcontrol.domain.repository.ControlRegistryRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ControlRegistryQueryService {

    private final ControlRegistryRepository repository;

    public ControlRegistryQueryService(ControlRegistryRepository repository) {
        this.repository = repository;
    }

    public ControlRegistryResult handle(GetControlRegistryQuery query) {
        return this.repository.findById(ControlRegistryId.of(query.id()))
                .map(ControlRegistryResult::from)
                .orElseThrow(() -> ControlRegistryNotFoundException.byId(query.id()));
    }

    public List<ControlRegistryResult> handle(GetControlRegistriesByFormatQuery query) {
        return this.repository.findAllByFormatId(ControlFormatId.of(query.formatId()))
                .stream()
                .map(ControlRegistryResult::from)
                .toList();
    }
}
