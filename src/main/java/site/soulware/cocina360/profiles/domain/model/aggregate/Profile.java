package site.soulware.cocina360.profiles.domain.model.aggregate;

import site.soulware.cocina360.profiles.domain.model.event.ProfileDetailsUpdated;
import site.soulware.cocina360.profiles.domain.model.valueobject.Email;
import site.soulware.cocina360.profiles.domain.model.valueobject.ProfileId;
import site.soulware.cocina360.shared.domain.model.aggregate.AggregateRoot;

import java.time.Instant;
import java.util.Objects;

public class Profile extends AggregateRoot<ProfileId> {

    private final ProfileId id;
    private String fullName;
    private String preferredName;
    private Email email;
    private String avatarUrl;
    private final Instant createdAt;
    private Instant updatedAt;

    private Profile(ProfileId id, String fullName, String preferredName, Email email,
                    String avatarUrl, Instant createdAt, Instant updatedAt) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.fullName = requireFullName(fullName);
        this.preferredName = normalizeOptional(preferredName);
        this.email = Objects.requireNonNull(email, "email must not be null");
        this.avatarUrl = normalizeOptional(avatarUrl);
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    public static Profile rehydrate(ProfileId id, String fullName, String preferredName,
                                    Email email, String avatarUrl, Instant createdAt, Instant updatedAt) {
        return new Profile(id, fullName, preferredName, email, avatarUrl, createdAt, updatedAt);
    }

    public void updateDetails(String fullName, String preferredName, String avatarUrl) {
        if (fullName != null) this.fullName = requireFullName(fullName);
        if (preferredName != null) this.preferredName = normalizeOptional(preferredName);
        if (avatarUrl != null) this.avatarUrl = normalizeOptional(avatarUrl);
        this.touch();
        this.registerEvent(new ProfileDetailsUpdated(this.id.value(), this.updatedAt));
    }

    private void touch() {
        this.updatedAt = Instant.now();
    }

    private static String requireFullName(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException("fullName must not be blank");
        }
        return fullName.trim();
    }

    private static String normalizeOptional(String value) {
        return (value == null || value.isBlank()) ? null : value.trim();
    }

    @Override
    public ProfileId getId() { return this.id; }
    public String getFullName() { return this.fullName; }
    public String getPreferredName() { return this.preferredName; }
    public Email getEmail() { return this.email; }
    public String getAvatarUrl() { return this.avatarUrl; }
    public Instant getCreatedAt() { return this.createdAt; }
    public Instant getUpdatedAt() { return this.updatedAt; }
}
