package site.soulware.cocina360.organizations.interfaces.rest.organization.response;

import site.soulware.cocina360.organizations.application.organization.OrganizationResult;
import site.soulware.cocina360.organizations.domain.model.valueobject.OrganizationAddress;

import java.time.Instant;
import java.util.UUID;

public record OrganizationResponse(
        UUID id,
        String name,
        String imageUrl,
        Address address,
        UUID ownedBy,
        UUID createdBy,
        Instant createdAt,
        UUID updatedBy,
        Instant updatedAt
) {
    public record Address(
            String lineOne,
            String lineTwo,
            String reference
    ) {
        public static Address from(OrganizationAddress address) {
            return new Address(address.lineOne(), address.lineTwo(), address.reference());
        }
    }

    public static OrganizationResponse from(OrganizationResult result) {
        return new OrganizationResponse(result.id(), result.name(), result.imageUrl(),
                Address.from(result.address()),
                result.ownedBy(), result.createdBy(), result.createdAt(),
                result.updatedBy(), result.updatedAt());
    }
}
