package site.soulware.cocina360.organizations.domain.model.aggregate;

import site.soulware.cocina360.organizations.domain.model.event.OrganizationCreated;
import site.soulware.cocina360.organizations.domain.model.event.OrganizationUpdated;
import site.soulware.cocina360.shared.domain.model.aggregate.AggregateRoot;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;

import java.time.Instant;

public class Organization extends AggregateRoot<OrganizationId> {

    private final OrganizationId id;
    private String name;
    private String imageUrl;
    private String addressLineOne;
    private String addressLineTwo;
    private String addressReference;
    private final Instant createdAt;
    private final ProfileId createdBy;
    private Instant updatedAt;
    private ProfileId updatedBy;
    private final ProfileId ownedBy;

    private Organization(
        OrganizationId id,
        String name,
        String imageUrl,
        String addressLineOne,
        String addressLineTwo,
        String addressReference,
        Instant createdAt,
        ProfileId createdBy,
        Instant updatedAt,
        ProfileId updatedBy,
        ProfileId ownedBy
    ) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.addressLineOne = addressLineOne;
        this.addressLineTwo = addressLineTwo;
        this.addressReference = addressReference;
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
        String addressLineOne,
        String addressLineTwo,
        String addressReference,
        ProfileId createdBy,
        ProfileId ownedBy
    ) {
        Instant now = Instant.now();
        Organization org = new Organization(id, name, imageUrl, addressLineOne, addressLineTwo,
                addressReference, now, createdBy, now, createdBy, ownedBy);
        org.registerEvent(new OrganizationCreated(id.value(), name, createdBy.value(), now));
        return org;
    }

    public static Organization rehydrate(
        OrganizationId id,
        String name,
        String imageUrl,
        String addressLineOne,
        String addressLineTwo,
        String addressReference,
        Instant createdAt,
        ProfileId createdBy,
        Instant updatedAt,
        ProfileId updatedBy,
        ProfileId ownedBy
    ) {
        return new Organization(id, name, imageUrl, addressLineOne, addressLineTwo, addressReference,
                createdAt, createdBy, updatedAt, updatedBy, ownedBy);
    }

    public void update(
        String name,
        String imageUrl,
        String addressLineOne,
        String addressLineTwo,
        String addressReference,
        ProfileId updatedBy
    ) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.addressLineOne = addressLineOne;
        this.addressLineTwo = addressLineTwo;
        this.addressReference = addressReference;
        this.updatedBy = updatedBy;
        this.updatedAt = Instant.now();
        this.registerEvent(new OrganizationUpdated(this.id.value(), this.updatedAt));
    }

    @Override
    public OrganizationId getId() { return this.id; }
    public String getName() { return this.name; }
    public String getImageUrl() { return this.imageUrl; }
    public String getAddressLineOne() { return this.addressLineOne; }
    public String getAddressLineTwo() { return this.addressLineTwo; }
    public String getAddressReference() { return this.addressReference; }
    public Instant getCreatedAt() { return this.createdAt; }
    public ProfileId getCreatedBy() { return this.createdBy; }
    public Instant getUpdatedAt() { return this.updatedAt; }
    public ProfileId getUpdatedBy() { return this.updatedBy; }
    public ProfileId getOwnedBy() { return this.ownedBy; }
}
