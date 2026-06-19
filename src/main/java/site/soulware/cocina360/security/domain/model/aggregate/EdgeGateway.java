package site.soulware.cocina360.security.domain.model.aggregate;

import site.soulware.cocina360.security.domain.model.event.EdgeGatewayApiKeyRotated;
import site.soulware.cocina360.security.domain.model.event.EdgeGatewayDeactivated;
import site.soulware.cocina360.security.domain.model.event.EdgeGatewayRegistered;
import site.soulware.cocina360.security.domain.model.valueobject.ActivationStatus;
import site.soulware.cocina360.security.domain.model.valueobject.ApiKey;
import site.soulware.cocina360.security.domain.model.valueobject.EdgeGatewayId;
import site.soulware.cocina360.shared.domain.model.aggregate.AggregateRoot;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;

import java.time.Instant;

/**
 * The backend's acknowledgement of the physical edge gateway that fronts an
 * organization's devices (1:1 with an organization). It is the {@code edge → backend}
 * trust anchor: the backend provisions its {@link ApiKey}, which the edge presents
 * to identify itself and pull its organization's device registry and thresholds.
 */
public class EdgeGateway extends AggregateRoot<EdgeGatewayId> {

    private final EdgeGatewayId id;
    private final OrganizationId organizationId;
    private String name;
    private ActivationStatus status;
    private ApiKey apiKey;
    private final Instant createdAt;
    private final ProfileId createdBy;
    private Instant updatedAt;
    private ProfileId updatedBy;

    private EdgeGateway(
        EdgeGatewayId id,
        OrganizationId organizationId,
        String name,
        ActivationStatus status,
        ApiKey apiKey,
        Instant createdAt,
        ProfileId createdBy,
        Instant updatedAt,
        ProfileId updatedBy
    ) {
        this.id = id;
        this.organizationId = organizationId;
        this.name = name;
        this.status = status;
        this.apiKey = apiKey;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
    }

    public static EdgeGateway register(
        EdgeGatewayId id,
        OrganizationId organizationId,
        String name,
        ProfileId requesterId
    ) {
        Instant now = Instant.now();
        EdgeGateway gateway = new EdgeGateway(id, organizationId, name, ActivationStatus.ACTIVE,
                ApiKey.generate(), now, requesterId, now, requesterId);
        gateway.registerEvent(new EdgeGatewayRegistered(id.value(), organizationId.value(), now));
        return gateway;
    }

    public static EdgeGateway rehydrate(
        EdgeGatewayId id,
        OrganizationId organizationId,
        String name,
        ActivationStatus status,
        ApiKey apiKey,
        Instant createdAt,
        ProfileId createdBy,
        Instant updatedAt,
        ProfileId updatedBy
    ) {
        return new EdgeGateway(id, organizationId, name, status, apiKey,
                createdAt, createdBy, updatedAt, updatedBy);
    }

    public void rename(String name, ProfileId updatedBy) {
        this.name = name;
        this.touch(updatedBy);
    }

    public void activate(ProfileId updatedBy) {
        this.status = ActivationStatus.ACTIVE;
        this.touch(updatedBy);
    }

    public void deactivate(ProfileId updatedBy) {
        this.status = ActivationStatus.INACTIVE;
        this.touch(updatedBy);
        this.registerEvent(new EdgeGatewayDeactivated(this.id.value(), this.updatedAt));
    }

    public void rotateApiKey(ProfileId updatedBy) {
        this.apiKey = ApiKey.generate();
        this.touch(updatedBy);
        this.registerEvent(new EdgeGatewayApiKeyRotated(this.id.value(), this.updatedAt));
    }

    private void touch(ProfileId updatedBy) {
        this.updatedBy = updatedBy;
        this.updatedAt = Instant.now();
    }

    @Override
    public EdgeGatewayId getId() { return this.id; }
    public OrganizationId getOrganizationId() { return this.organizationId; }
    public String getName() { return this.name; }
    public ActivationStatus getStatus() { return this.status; }
    public ApiKey getApiKey() { return this.apiKey; }
    public Instant getCreatedAt() { return this.createdAt; }
    public ProfileId getCreatedBy() { return this.createdBy; }
    public Instant getUpdatedAt() { return this.updatedAt; }
    public ProfileId getUpdatedBy() { return this.updatedBy; }
}
