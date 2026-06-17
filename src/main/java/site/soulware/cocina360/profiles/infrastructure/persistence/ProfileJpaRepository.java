package site.soulware.cocina360.profiles.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProfileJpaRepository extends JpaRepository<ProfileJpaEntity, UUID> {

    Optional<ProfileJpaEntity> findByEmail(String email);
}
