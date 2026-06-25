package site.soulware.cocina360.internalcontrol.infrastructure.persistence.controlformat.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ControlFormatFieldJpaRepository extends JpaRepository<ControlFormatFieldJpaEntity, UUID> {

    List<ControlFormatFieldJpaEntity> findAllByFormatId(UUID formatId);

    List<ControlFormatFieldJpaEntity> findAllByFormatIdIn(Collection<UUID> formatIds);
}
