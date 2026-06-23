package site.soulware.cocina360.profiles.application.profile;

import org.springframework.stereotype.Service;
import site.soulware.cocina360.profiles.domain.model.query.GetProfileByEmailQuery;
import site.soulware.cocina360.profiles.domain.model.query.GetProfileQuery;
import site.soulware.cocina360.profiles.domain.model.query.ListProfilesByIdsQuery;
import site.soulware.cocina360.profiles.interfaces.acl.ProfileSummary;
import site.soulware.cocina360.profiles.interfaces.acl.ProfilesApi;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
class ProfilesApiImpl implements ProfilesApi {

    private final ProfileQueryService queryService;

    ProfilesApiImpl(ProfileQueryService queryService) {
        this.queryService = queryService;
    }

    @Override
    public ProfileId requireProfileId(UUID profileId) {
        return ProfileId.of(this.queryService.handle(new GetProfileQuery(profileId)).profileId());
    }

    @Override
    public ProfileId requireProfileIdByEmail(String email) {
        return ProfileId.of(this.queryService.handle(new GetProfileByEmailQuery(email)).profileId());
    }

    @Override
    public String requireEmailByProfileId(UUID profileId) {
        return this.queryService.handle(new GetProfileQuery(profileId)).email();
    }

    @Override
    public Map<UUID, ProfileSummary> findProfiles(Collection<UUID> profileIds) {
        return this.queryService.handle(new ListProfilesByIdsQuery(profileIds)).stream()
                .map(result -> new ProfileSummary(
                        result.profileId(),
                        result.fullName(),
                        result.preferredName(),
                        result.email(),
                        result.avatarUrl()))
                .collect(Collectors.toMap(ProfileSummary::id, Function.identity()));
    }
}
