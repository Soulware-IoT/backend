package site.soulware.cocina360.profiles.application;

import org.springframework.stereotype.Service;
import site.soulware.cocina360.profiles.domain.model.query.GetProfileByEmailQuery;
import site.soulware.cocina360.profiles.domain.model.query.GetProfileQuery;
import site.soulware.cocina360.profiles.interfaces.acl.ProfilesApi;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;

import java.util.UUID;

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
}
