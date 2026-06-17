package site.soulware.cocina360.profiles.interfaces.rest.response;

import site.soulware.cocina360.profiles.application.ProfileResult;

import java.time.Instant;
import java.util.UUID;

public record ProfileResponse(
        UUID profileId,
        String fullName,
        String preferredName,
        String email,
        String avatarUrl,
        Instant createdAt,
        Instant updatedAt
) {

    public static ProfileResponse from(ProfileResult result) {
        return new ProfileResponse(
                result.profileId(),
                result.fullName(),
                result.preferredName(),
                result.email(),
                result.avatarUrl(),
                result.createdAt(),
                result.updatedAt()
        );
    }
}
