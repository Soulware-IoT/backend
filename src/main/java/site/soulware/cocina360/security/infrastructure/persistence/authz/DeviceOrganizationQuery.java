package site.soulware.cocina360.security.infrastructure.persistence.authz;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import site.soulware.cocina360.security.infrastructure.persistence.iotdevice.jpa.IoTDeviceJpaEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * Resolves the owning organization of a security resource for authorization, by id. Intra-context
 * native lookups (security schema only) — the requester's permission in the returned org is then
 * checked via {@code AuthorizationApi}.
 */
public interface DeviceOrganizationQuery extends Repository<IoTDeviceJpaEntity, UUID> {

    @Query(value = "SELECT organization_id FROM iot_devices WHERE id = :id", nativeQuery = true)
    Optional<UUID> findIotDeviceOrganization(@Param("id") UUID iotDeviceId);

    @Query(value = "SELECT organization_id FROM edge_devices WHERE id = :id", nativeQuery = true)
    Optional<UUID> findEdgeDeviceOrganization(@Param("id") UUID edgeDeviceId);
}
