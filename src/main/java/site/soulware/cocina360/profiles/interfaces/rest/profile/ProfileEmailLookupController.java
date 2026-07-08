package site.soulware.cocina360.profiles.interfaces.rest.profile;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import site.soulware.cocina360.profiles.application.profile.ProfileQueryService;
import site.soulware.cocina360.profiles.domain.model.query.GetProfileByEmailQuery;
import site.soulware.cocina360.profiles.interfaces.rest.profile.response.ProfileResponse;

/**
 * Profile lookup by email — {@code GET /profiles?email=...}.
 * <p>
 * Held in its own controller so it can be environment-gated independently of the rest of
 * {@link ProfileController}: an email lookup enables email enumeration, so it is not exposed by
 * default. Gated by {@code app.profiles.email-lookup.enabled} (default {@code false}): where the
 * flag is off the controller is not a bean, so its route is never registered and the endpoint
 * returns 404 — it does not exist in that environment, not merely hidden. Enable it only where the
 * lookup is actually needed (e.g. dev/tooling).
 */
@RestController
@ConditionalOnProperty(name = "app.profiles.email-lookup.enabled", havingValue = "true")
@RequestMapping("/profiles")
public class ProfileEmailLookupController {

    private final ProfileQueryService queryService;

    public ProfileEmailLookupController(ProfileQueryService queryService) {
        this.queryService = queryService;
    }

    @GetMapping
    public ResponseEntity<ProfileResponse> getByEmail(@RequestParam String email) {
        return ResponseEntity.ok(
                ProfileResponse.from(this.queryService.handle(new GetProfileByEmailQuery(email)))
        );
    }
}
