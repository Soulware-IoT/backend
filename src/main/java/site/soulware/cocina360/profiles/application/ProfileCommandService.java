package site.soulware.cocina360.profiles.application;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.profiles.domain.model.aggregate.Profile;
import site.soulware.cocina360.profiles.domain.model.command.UpdateProfileDetailsCommand;
import site.soulware.cocina360.profiles.domain.repository.ProfileRepository;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;
import site.soulware.cocina360.profiles.domain.model.exception.ProfileNotFoundException;

@Service
@Transactional
public class ProfileCommandService {

    private final ProfileRepository profileRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ProfileCommandService(ProfileRepository profileRepository, ApplicationEventPublisher eventPublisher) {
        this.profileRepository = profileRepository;
        this.eventPublisher = eventPublisher;
    }

    public void handle(UpdateProfileDetailsCommand command) {
        Profile profile = this.findOrThrow(ProfileId.of(command.profileId()));

        profile.updateDetails(command.fullName(), command.preferredName(), command.avatarUrl());

        this.profileRepository.save(profile);
        profile.pullDomainEvents().forEach(this.eventPublisher::publishEvent);
    }

    private Profile findOrThrow(ProfileId id) {
        return this.profileRepository.findById(id)
                .orElseThrow(() -> ProfileNotFoundException.byId(id.value()));
    }
}
