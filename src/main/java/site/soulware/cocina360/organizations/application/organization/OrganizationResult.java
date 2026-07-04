package site.soulware.cocina360.organizations.application.organization;

import site.soulware.cocina360.organizations.domain.model.aggregate.Organization;
import site.soulware.cocina360.organizations.domain.model.valueobject.OrganizationAddress;
import site.soulware.cocina360.profiles.interfaces.acl.ProfileSummary;

import java.time.Instant;
import java.util.UUID;

public record OrganizationResult(
        UUID id,
        String name,
        String imageUrl,
        OrganizationAddress address,
        UUID ownedBy,
        ProfileSummary owner,
        UUID createdBy,
        Instant createdAt,
        UUID updatedBy,
        Instant updatedAt
) {
    public static OrganizationResult from(Organization org, ProfileSummary owner) {
        return new OrganizationResult(
                org.getId().value(),
                org.getName(),
                org.getImageUrl(),
                org.getAddress(),
                org.getOwnedBy().value(),
                owner,
                org.getCreatedBy().value(),
                org.getCreatedAt(),
                org.getUpdatedBy().value(),
                org.getUpdatedAt()
        );
    }
}
