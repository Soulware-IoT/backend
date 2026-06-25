package site.soulware.cocina360.internalcontrol.infrastructure.persistence.controlprocess.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ControlProcessJpaRepository extends JpaRepository<ControlProcessJpaEntity, UUID> {

    List<ControlProcessJpaEntity> findAllByOrganizationId(UUID organizationId);
}
