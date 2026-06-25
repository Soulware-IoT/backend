package site.soulware.cocina360.internalcontrol.infrastructure.persistence.controlformat.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ControlFormatJpaRepository extends JpaRepository<ControlFormatJpaEntity, UUID> {

    List<ControlFormatJpaEntity> findAllByProcessId(UUID processId);
}
