package site.soulware.cocina360.organizations.interfaces.rest.response;

import site.soulware.cocina360.organizations.application.OrganizationResult;

import java.time.Instant;
import java.util.UUID;

public record OrganizationResponse(
        UUID id,
        String name,
        String imageUrl,
        String addressLineOne,
        String addressLineTwo,
        String addressReference,
        UUID ownedBy,
        UUID createdBy,
        Instant createdAt,
        UUID updatedBy,
        Instant updatedAt
) {
    public static OrganizationResponse from(OrganizationResult result) {
        return new OrganizationResponse(result.id(), result.name(), result.imageUrl(),
                result.addressLineOne(), result.addressLineTwo(), result.addressReference(),
                result.ownedBy(), result.createdBy(), result.createdAt(),
                result.updatedBy(), result.updatedAt());
    }
}
