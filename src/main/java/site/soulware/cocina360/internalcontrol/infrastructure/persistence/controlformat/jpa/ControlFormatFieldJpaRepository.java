package site.soulware.cocina360.internalcontrol.infrastructure.persistence.controlformat.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ControlFormatFieldJpaRepository extends JpaRepository<ControlFormatFieldJpaEntity, UUID> {

    List<ControlFormatFieldJpaEntity> findAllByFormatId(UUID formatId);

    List<ControlFormatFieldJpaEntity> findAllByFormatIdIn(Collection<UUID> formatIds);

    @Modifying
    @Query("delete from ControlFormatFieldJpaEntity f where f.formatId = :formatId")
    void deleteByFormatId(@Param("formatId") UUID formatId);
}
