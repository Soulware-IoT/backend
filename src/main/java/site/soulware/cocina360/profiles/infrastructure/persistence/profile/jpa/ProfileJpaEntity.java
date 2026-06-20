package site.soulware.cocina360.profiles.infrastructure.persistence.profile.jpa;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "profiles", schema = "public")
public class ProfileJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "preferred_name")
    private String preferredName;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected ProfileJpaEntity() {
    }

    public ProfileJpaEntity(UUID id, String fullName, String preferredName, String email,
                            String avatarUrl, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.fullName = fullName;
        this.preferredName = preferredName;
        this.email = email;
        this.avatarUrl = avatarUrl;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public UUID getId() { return this.id; }
    public String getFullName() { return this.fullName; }
    public String getPreferredName() { return this.preferredName; }
    public String getEmail() { return this.email; }
    public String getAvatarUrl() { return this.avatarUrl; }
    public Instant getCreatedAt() { return this.createdAt; }
    public Instant getUpdatedAt() { return this.updatedAt; }
}
