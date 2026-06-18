package site.soulware.cocina360.organizations.domain.repository;

import site.soulware.cocina360.organizations.domain.model.aggregate.Organization;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.repository.DomainRepository;

public interface OrganizationRepository extends DomainRepository<Organization, OrganizationId> {}
