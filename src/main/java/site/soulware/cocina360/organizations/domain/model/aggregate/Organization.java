package site.soulware.cocina360.organizations.domain.model.aggregate;

import site.soulware.cocina360.organizations.domain.model.event.OrganizationCreated;
import site.soulware.cocina360.organizations.domain.model.event.OrganizationUpdated;
import site.soulware.cocina360.organizations.domain.model.exception.NotOrganizationOwnerException;
import site.soulware.cocina360.organizations.domain.model.valueobject.OrganizationAddress;
import site.soulware.cocina360.shared.domain.model.aggregate.AggregateRoot;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;

import java.time.Instant;

public class Organization extends AggregateRoot<OrganizationId> {

    private final OrganizationId id;
    private String name;
    private String imageUrl;
    private OrganizationAddress address;
    private final Instant createdAt;
    private final ProfileId createdBy;
    private Instant updatedAt;
    private ProfileId updatedBy;
    private final ProfileId ownedBy;

    private Organization(
        OrganizationId id,
        String name,
        String imageUrl,
        OrganizationAddress address,
        Instant createdAt,
        ProfileId createdBy,
        Instant updatedAt,
        ProfileId updatedBy,
        ProfileId ownedBy
    ) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.address = address;
        this.createdAt = createdAt;
        this.createdBy = createdBy;
        this.updatedAt = updatedAt;
        this.updatedBy = updatedBy;
        this.ownedBy = ownedBy;
    }

    public static Organization create(
        OrganizationId id,
        String name,
        String imageUrl,
        OrganizationAddress address,
        ProfileId createdBy,
        ProfileId ownedBy
    ) {
        Instant now = Instant.now();
        Organization org = new Organization(id, name, imageUrl, address, now, createdBy, now, createdBy, ownedBy);
        org.registerEvent(new OrganizationCreated(id.value(), name, createdBy.value(), now));
        return org;
    }

    public static Organization rehydrate(
        OrganizationId id,
        String name,
        String imageUrl,
        OrganizationAddress address,
        Instant createdAt,
        ProfileId createdBy,
        Instant updatedAt,
        ProfileId updatedBy,
        ProfileId ownedBy
    ) {
        return new Organization(id, name, imageUrl, address, createdAt, createdBy, updatedAt, updatedBy, ownedBy);
    }

    public void update(
        String name,
        String imageUrl,
        OrganizationAddress address,
        ProfileId updatedBy
    ) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.address = address;
        this.updatedBy = updatedBy;
        this.updatedAt = Instant.now();
        this.registerEvent(new OrganizationUpdated(this.id.value(), this.updatedAt));
    }

    public void requireOwner(ProfileId requesterId) {
        if (!this.ownedBy.equals(requesterId)) {
            throw new NotOrganizationOwnerException();
        }
    }

    @Override
    public OrganizationId getId() { return this.id; }
    public String getName() { return this.name; }
    public String getImageUrl() { return this.imageUrl; }
    public OrganizationAddress getAddress() { return this.address; }
    public Instant getCreatedAt() { return this.createdAt; }
    public ProfileId getCreatedBy() { return this.createdBy; }
    public Instant getUpdatedAt() { return this.updatedAt; }
    public ProfileId getUpdatedBy() { return this.updatedBy; }
    public ProfileId getOwnedBy() { return this.ownedBy; }
}
