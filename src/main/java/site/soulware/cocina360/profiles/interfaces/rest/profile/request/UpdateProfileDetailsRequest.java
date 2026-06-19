package site.soulware.cocina360.profiles.interfaces.rest.profile.request;

import io.swagger.v3.oas.annotations.media.Schema;
import site.soulware.cocina360.profiles.domain.model.command.UpdateProfileDetailsCommand;

import java.util.UUID;

public record UpdateProfileDetailsRequest(
        @Schema(description = "Optional. Provide to update; omit to leave unchanged.") String fullName,
        @Schema(description = "Optional. Provide to update; omit to leave unchanged.") String preferredName,
        @Schema(description = "Optional. Provide to update; omit to leave unchanged.") String avatarUrl
) {

    public UpdateProfileDetailsCommand toCommand(UUID profileId) {
        return new UpdateProfileDetailsCommand(profileId, this.fullName, this.preferredName, this.avatarUrl);
    }
}
