package site.soulware.cocina360.profiles.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.profiles.domain.model.aggregate.Profile;
import site.soulware.cocina360.profiles.domain.model.query.GetProfileByEmailQuery;
import site.soulware.cocina360.profiles.domain.model.query.GetProfileQuery;
import site.soulware.cocina360.profiles.domain.model.valueobject.Email;
import site.soulware.cocina360.profiles.domain.repository.ProfileRepository;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;
import site.soulware.cocina360.profiles.domain.model.exception.ProfileNotFoundException;

@Service
@Transactional(readOnly = true)
public class ProfileQueryService {

    private final ProfileRepository profileRepository;

    public ProfileQueryService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    public ProfileResult handle(GetProfileQuery query) {
        Profile profile = this.profileRepository.findById(ProfileId.of(query.profileId()))
                .orElseThrow(() -> ProfileNotFoundException.byId(query.profileId()));
        return ProfileResult.from(profile);
    }

    public ProfileResult handle(GetProfileByEmailQuery query) {
        Profile profile = this.profileRepository.findByEmail(new Email(query.email()))
                .orElseThrow(() -> ProfileNotFoundException.byEmail(query.email()));
        return ProfileResult.from(profile);
    }
}
