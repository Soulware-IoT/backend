package site.soulware.cocina360.organizations.application.organization;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.organizations.domain.model.exception.OrganizationNotFoundException;
import site.soulware.cocina360.organizations.domain.model.query.GetOrganizationQuery;
import site.soulware.cocina360.organizations.domain.repository.OrganizationRepository;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;

@Service
@Transactional(readOnly = true)
public class OrganizationQueryService {

    private final OrganizationRepository organizationRepository;

    public OrganizationQueryService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    public OrganizationResult handle(GetOrganizationQuery query) {
        return this.organizationRepository.findById(OrganizationId.of(query.organizationId()))
                .map(OrganizationResult::from)
                .orElseThrow(() -> OrganizationNotFoundException.byId(query.organizationId()));
    }
}
