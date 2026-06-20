package site.soulware.cocina360.profiles.interfaces.rest.profile;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.soulware.cocina360.profiles.application.profile.ProfileCommandService;
import site.soulware.cocina360.profiles.application.profile.ProfileQueryService;
import site.soulware.cocina360.profiles.domain.model.query.GetProfileByEmailQuery;
import site.soulware.cocina360.profiles.domain.model.query.GetProfileQuery;
import site.soulware.cocina360.profiles.interfaces.rest.profile.request.UpdateProfileDetailsRequest;
import site.soulware.cocina360.profiles.interfaces.rest.profile.response.ProfileResponse;

import java.util.UUID;

@RestController
@RequestMapping("/profiles")
public class ProfileController {

    private final ProfileCommandService commandService;
    private final ProfileQueryService queryService;

    public ProfileController(ProfileCommandService commandService, ProfileQueryService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(
                ProfileResponse.from(this.queryService.handle(new GetProfileQuery(id)))
        );
    }

    @GetMapping
    public ResponseEntity<ProfileResponse> getByEmail(@RequestParam String email) {
        return ResponseEntity.ok(
                ProfileResponse.from(this.queryService.handle(new GetProfileByEmailQuery(email)))
        );
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProfileResponse> updateDetails(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateProfileDetailsRequest request,
            @RequestHeader("X-Requester-Id") UUID requesterId) {

        this.commandService.handle(request.toCommand(id, requesterId));

        return ResponseEntity.ok(
                ProfileResponse.from(this.queryService.handle(new GetProfileQuery(id)))
        );
    }
}
