package site.soulware.cocina360.organizations.application.organization;

import site.soulware.cocina360.organizations.domain.model.aggregate.Organization;
import site.soulware.cocina360.organizations.domain.model.valueobject.OrganizationAddress;

import java.time.Instant;
import java.util.UUID;

public record OrganizationResult(
        UUID id,
        String name,
        String imageUrl,
        OrganizationAddress address,
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
                org.getAddress(),
                org.getOwnedBy().value(),
                org.getCreatedBy().value(),
                org.getCreatedAt(),
                org.getUpdatedBy().value(),
                org.getUpdatedAt()
        );
    }
}
