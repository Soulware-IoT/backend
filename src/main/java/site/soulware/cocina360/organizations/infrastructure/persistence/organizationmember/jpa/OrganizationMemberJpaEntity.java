package site.soulware.cocina360.organizations.infrastructure.persistence.organizationmember.jpa;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.SecondaryTable;
import jakarta.persistence.SecondaryTables;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnTransformer;
import site.soulware.cocina360.organizations.domain.model.valueobject.PermissionLevel;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "organization_members")
@SecondaryTables({
    @SecondaryTable(
        name = "organization_member_permissions",
        pkJoinColumns = @PrimaryKeyJoinColumn(name = "organization_member_id")
    )
})
public class OrganizationMemberJpaEntity {

    @Id
    private UUID id;

    @Column(name = "profile_id", nullable = false)
    private UUID profileId;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "invitation_id")
    private UUID invitationId;

    @Column(name = "joined_at", nullable = false, updatable = false)
    private Instant joinedAt;

    @Column(table = "organization_member_permissions", name = "security", nullable = false)
    @ColumnTransformer(write = "?::permission_level")
    private PermissionLevel security;

    @Column(table = "organization_member_permissions", name = "iot", nullable = false)
    @ColumnTransformer(write = "?::permission_level")
    private PermissionLevel iot;

    @Column(table = "organization_member_permissions", name = "internal_control", nullable = false)
    @ColumnTransformer(write = "?::permission_level")
    private PermissionLevel internalControl;

    protected OrganizationMemberJpaEntity() {}

    public OrganizationMemberJpaEntity(
        UUID id,
        UUID profileId,
        UUID organizationId,
        UUID invitationId,
        Instant joinedAt,
        PermissionLevel security,
        PermissionLevel iot,
        PermissionLevel internalControl
    ) {
        this.id = id;
        this.profileId = profileId;
        this.organizationId = organizationId;
        this.invitationId = invitationId;
        this.joinedAt = joinedAt;
        this.security = security;
        this.iot = iot;
        this.internalControl = internalControl;
    }

    public UUID getId() { return this.id; }
    public UUID getProfileId() { return this.profileId; }
    public UUID getOrganizationId() { return this.organizationId; }
    public UUID getInvitationId() { return this.invitationId; }
    public Instant getJoinedAt() { return this.joinedAt; }
    public PermissionLevel getSecurity() { return this.security; }
    public PermissionLevel getIot() { return this.iot; }
    public PermissionLevel getInternalControl() { return this.internalControl; }
}
