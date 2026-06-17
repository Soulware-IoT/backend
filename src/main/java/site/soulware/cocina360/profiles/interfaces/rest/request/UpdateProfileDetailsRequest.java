package site.soulware.cocina360.profiles.interfaces.rest.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import site.soulware.cocina360.profiles.domain.model.command.UpdateProfileDetailsCommand;

import java.util.UUID;

public record UpdateProfileDetailsRequest(
        String fullName,
        String preferredName,
        String avatarUrl
) {

    @JsonIgnore
    @AssertTrue(message = "{error.patch.no_fields}")
    public boolean isAtLeastOneFieldPresent() {
        return this.fullName != null || this.preferredName != null || this.avatarUrl != null;
    }

    public UpdateProfileDetailsCommand toCommand(UUID profileId) {
        return new UpdateProfileDetailsCommand(profileId, this.fullName, this.preferredName, this.avatarUrl);
    }
}
