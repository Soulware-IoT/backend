package site.soulware.cocina360.organizations.infrastructure.persistence.organization;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "organizations")
public class OrganizationJpaEntity {

    @Id
    private UUID id;

    @Column(name = "created_by", nullable = false, updatable = false)
    private UUID createdBy;

    @Column(name = "updated_by", nullable = false)
    private UUID updatedBy;

    @Column(name = "owned_by", nullable = false)
    private UUID ownedBy;

    @Column(nullable = false)
    private String name;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "address_line_one")
    private String addressLineOne;

    @Column(name = "address_line_two")
    private String addressLineTwo;

    @Column(name = "address_reference")
    private String addressReference;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected OrganizationJpaEntity() {}

    public OrganizationJpaEntity(
        UUID id,
        UUID createdBy,
        UUID updatedBy,
        UUID ownedBy,
        String name,
        String imageUrl,
        String addressLineOne,
        String addressLineTwo,
        String addressReference,
        Instant createdAt,
        Instant updatedAt
    ) {
        this.id = id;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.ownedBy = ownedBy;
        this.name = name;
        this.imageUrl = imageUrl;
        this.addressLineOne = addressLineOne;
        this.addressLineTwo = addressLineTwo;
        this.addressReference = addressReference;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return this.id; }
    public UUID getCreatedBy() { return this.createdBy; }
    public UUID getUpdatedBy() { return this.updatedBy; }
    public UUID getOwnedBy() { return this.ownedBy; }
    public String getName() { return this.name; }
    public String getImageUrl() { return this.imageUrl; }
    public String getAddressLineOne() { return this.addressLineOne; }
    public String getAddressLineTwo() { return this.addressLineTwo; }
    public String getAddressReference() { return this.addressReference; }
    public Instant getCreatedAt() { return this.createdAt; }
    public Instant getUpdatedAt() { return this.updatedAt; }
}
