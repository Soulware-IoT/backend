package site.soulware.cocina360.profiles.interfaces.rest;

import jakarta.validation.constraints.NotBlank;
import site.soulware.cocina360.profiles.domain.model.command.UpdateProfileDetailsCommand;

import java.util.UUID;

public record UpdateProfileDetailsRequest(
        @NotBlank String fullName,
        String preferredName,
        String avatarUrl
) {

    public UpdateProfileDetailsCommand toCommand(UUID profileId) {
        return new UpdateProfileDetailsCommand(profileId, this.fullName, this.preferredName, this.avatarUrl);
    }
}
