package site.soulware.cocina360.organizations.infrastructure.persistence.organization.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrganizationJpaRepository extends JpaRepository<OrganizationJpaEntity, UUID> {}
