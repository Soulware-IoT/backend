package site.soulware.cocina360.organizations.application.organization;

import org.springframework.stereotype.Service;
import site.soulware.cocina360.organizations.domain.model.query.GetOrganizationQuery;
import site.soulware.cocina360.organizations.interfaces.acl.OrganizationsApi;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;

import java.util.UUID;

@Service
class OrganizationsApiImpl implements OrganizationsApi {

    private final OrganizationQueryService queryService;

    OrganizationsApiImpl(OrganizationQueryService queryService) {
        this.queryService = queryService;
    }

    @Override
    public OrganizationId requireOrganizationId(UUID organizationId) {
        return OrganizationId.of(this.queryService.handle(new GetOrganizationQuery(organizationId)).id());
    }
}
