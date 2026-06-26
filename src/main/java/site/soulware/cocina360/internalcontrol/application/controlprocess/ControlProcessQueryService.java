package site.soulware.cocina360.internalcontrol.application.controlprocess;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.internalcontrol.domain.model.exception.ControlProcessNotFoundException;
import site.soulware.cocina360.internalcontrol.domain.model.query.GetControlProcessQuery;
import site.soulware.cocina360.internalcontrol.domain.model.query.GetControlProcessesByOrganizationQuery;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlProcessId;
import site.soulware.cocina360.internalcontrol.domain.repository.ControlProcessRepository;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ControlProcessQueryService {

    private final ControlProcessRepository repository;

    public ControlProcessQueryService(ControlProcessRepository repository) {
        this.repository = repository;
    }

    public ControlProcessResult handle(GetControlProcessQuery query) {
        return this.repository.findById(ControlProcessId.of(query.id()))
                .map(ControlProcessResult::from)
                .orElseThrow(() -> ControlProcessNotFoundException.byId(query.id()));
    }

    public List<ControlProcessResult> handle(GetControlProcessesByOrganizationQuery query) {
        return this.repository.findAllByOrganizationId(OrganizationId.of(query.organizationId()))
                .stream()
                .map(ControlProcessResult::from)
                .toList();
    }
}
