package site.soulware.cocina360.profiles.domain.repository;

import site.soulware.cocina360.profiles.domain.model.aggregate.Profile;
import site.soulware.cocina360.profiles.domain.model.valueobject.Email;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;
import site.soulware.cocina360.shared.domain.repository.DomainRepository;

import java.util.Optional;

public interface ProfileRepository extends DomainRepository<Profile, ProfileId> {

    // save(Profile), findById(ProfileId) and delete(Profile) are inherited from DomainRepository.

    Optional<Profile> findByEmail(Email email);

    boolean existsById(ProfileId id);
}
