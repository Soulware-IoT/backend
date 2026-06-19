package site.soulware.cocina360.security.infrastructure.persistence.iotdevice;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IoTDeviceJpaRepository extends JpaRepository<IoTDeviceJpaEntity, UUID> {

    Optional<IoTDeviceJpaEntity> findByCode(String code);

    List<IoTDeviceJpaEntity> findByOrganizationId(UUID organizationId);

    boolean existsByCode(String code);
}
