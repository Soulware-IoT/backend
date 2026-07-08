package site.soulware.cocina360.organizations.infrastructure.persistence.organizationmember.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrganizationMemberJpaRepository extends JpaRepository<OrganizationMemberJpaEntity, UUID> {

    List<OrganizationMemberJpaEntity> findAllByOrganizationId(UUID organizationId);

    List<OrganizationMemberJpaEntity> findAllByProfileId(UUID profileId);

    Optional<OrganizationMemberJpaEntity> findByOrganizationIdAndProfileId(UUID organizationId, UUID profileId);
}
