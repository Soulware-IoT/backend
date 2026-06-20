package site.soulware.cocina360.security.infrastructure.persistence.edgedevice.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EdgeDeviceJpaRepository extends JpaRepository<EdgeDeviceJpaEntity, UUID> {

    Optional<EdgeDeviceJpaEntity> findByOrganizationId(UUID organizationId);

    Optional<EdgeDeviceJpaEntity> findByApiKey(String apiKey);

    Optional<EdgeDeviceJpaEntity> findByCode(String code);

    boolean existsByOrganizationId(UUID organizationId);

    boolean existsByCode(String code);
}
