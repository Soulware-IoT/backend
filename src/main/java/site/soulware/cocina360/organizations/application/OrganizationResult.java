package site.soulware.cocina360.organizations.application;

import site.soulware.cocina360.organizations.domain.model.aggregate.Organization;
import site.soulware.cocina360.organizations.domain.model.valueobject.Location;

import java.time.Instant;
import java.util.UUID;

public record OrganizationResult(
        UUID id,
        String name,
        String imageUrl,
        String addressLineOne,
        String addressLineTwo,
        String addressReference,
        Location location,
        UUID ownedBy,
        UUID createdBy,
        Instant createdAt,
        UUID updatedBy,
        Instant updatedAt
) {
    public static OrganizationResult from(Organization org) {
        return new OrganizationResult(
                org.getId().value(),
                org.getName(),
                org.getImageUrl(),
                org.getAddressLineOne(),
                org.getAddressLineTwo(),
                org.getAddressReference(),
                org.getLocation(),
                org.getOwnedBy().value(),
                org.getCreatedBy().value(),
                org.getCreatedAt(),
                org.getUpdatedBy().value(),
                org.getUpdatedAt()
        );
    }
}
