package site.soulware.cocina360.profiles.domain.model.command;

import java.util.UUID;

public record UpdateProfileDetailsCommand(
        UUID profileId,
        UUID requesterId,
        String fullName,
        String preferredName,
        String avatarUrl
) {
}
