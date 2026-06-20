package site.soulware.cocina360.organizations.infrastructure.persistence.organizationmember;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OrganizationMemberJpaRepository extends JpaRepository<OrganizationMemberJpaEntity, UUID> {

    List<OrganizationMemberJpaEntity> findAllByOrganizationId(UUID organizationId);

    List<OrganizationMemberJpaEntity> findAllByProfileId(UUID profileId);
}
