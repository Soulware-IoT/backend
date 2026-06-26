package site.soulware.cocina360.internalcontrol.infrastructure.persistence.controlregistry.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ControlRegistryJpaRepository extends JpaRepository<ControlRegistryJpaEntity, UUID> {

    List<ControlRegistryJpaEntity> findAllByFormatId(UUID formatId);
}
