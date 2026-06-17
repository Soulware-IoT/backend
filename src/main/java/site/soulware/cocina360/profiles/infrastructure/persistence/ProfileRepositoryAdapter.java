package site.soulware.cocina360.profiles.infrastructure.persistence;

import org.springframework.stereotype.Repository;
import site.soulware.cocina360.profiles.domain.model.aggregate.Profile;
import site.soulware.cocina360.profiles.domain.model.valueobject.Email;
import site.soulware.cocina360.profiles.domain.model.valueobject.ProfileId;
import site.soulware.cocina360.profiles.domain.repository.ProfileRepository;

import java.util.Optional;

@Repository
public class ProfileRepositoryAdapter implements ProfileRepository {

    private final ProfileJpaRepository jpaRepository;

    public ProfileRepositoryAdapter(ProfileJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Profile save(Profile aggregate) {
        ProfileJpaEntity saved = this.jpaRepository.save(this.toJpaEntity(aggregate));
        return this.toDomain(saved);
    }

    @Override
    public Optional<Profile> findById(ProfileId id) {
        return this.jpaRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public Optional<Profile> findByEmail(Email email) {
        return this.jpaRepository.findByEmail(email.value()).map(this::toDomain);
    }

    @Override
    public boolean existsById(ProfileId id) {
        return this.jpaRepository.existsById(id.value());
    }

    @Override
    public void delete(Profile aggregate) {
        this.jpaRepository.deleteById(aggregate.getId().value());
    }

    private ProfileJpaEntity toJpaEntity(Profile profile) {
        return new ProfileJpaEntity(
                profile.getId().value(),
                profile.getFullName(),
                profile.getPreferredName(),
                profile.getEmail().value(),
                profile.getAvatarUrl(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }

    private Profile toDomain(ProfileJpaEntity entity) {
        return Profile.rehydrate(
                ProfileId.of(entity.getId()),
                entity.getFullName(),
                entity.getPreferredName(),
                new Email(entity.getEmail()),
                entity.getAvatarUrl(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
