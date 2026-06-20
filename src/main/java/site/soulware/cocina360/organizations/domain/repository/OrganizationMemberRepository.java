package site.soulware.cocina360.organizations.domain.repository;

import site.soulware.cocina360.organizations.domain.model.aggregate.OrganizationMember;
import site.soulware.cocina360.organizations.domain.model.valueobject.OrganizationMemberId;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;
import site.soulware.cocina360.shared.domain.repository.DomainRepository;

import java.util.List;

public interface OrganizationMemberRepository extends DomainRepository<OrganizationMember, OrganizationMemberId> {

    List<OrganizationMember> findAllByOrganizationId(OrganizationId organizationId);

    List<OrganizationMember> findAllByProfileId(ProfileId profileId);
}
