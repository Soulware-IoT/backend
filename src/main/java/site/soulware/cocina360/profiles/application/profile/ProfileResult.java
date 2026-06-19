package site.soulware.cocina360.profiles.application.profile;

import site.soulware.cocina360.profiles.domain.model.aggregate.Profile;

import java.time.Instant;
import java.util.UUID;

public record ProfileResult(
        UUID profileId,
        String fullName,
        String preferredName,
        String email,
        String avatarUrl,
        Instant createdAt,
        Instant updatedAt
) {

    public static ProfileResult from(Profile profile) {
        return new ProfileResult(
                profile.getId().value(),
                profile.getFullName(),
                profile.getPreferredName(),
                profile.getEmail().value(),
                profile.getAvatarUrl(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}
