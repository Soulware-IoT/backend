package site.soulware.cocina360.security.infrastructure.persistence.edgegateway;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EdgeGatewayJpaRepository extends JpaRepository<EdgeGatewayJpaEntity, UUID> {

    Optional<EdgeGatewayJpaEntity> findByOrganizationId(UUID organizationId);

    boolean existsByOrganizationId(UUID organizationId);
}
