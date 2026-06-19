package site.soulware.cocina360.security.infrastructure.persistence.device;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DeviceJpaRepository extends JpaRepository<DeviceJpaEntity, UUID> {

    Optional<DeviceJpaEntity> findByCode(String code);

    List<DeviceJpaEntity> findByOrganizationId(UUID organizationId);

    boolean existsByCode(String code);
}
