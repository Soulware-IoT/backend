package site.soulware.cocina360.organizations.infrastructure.persistence.invitation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.ColumnTransformer;
import site.soulware.cocina360.organizations.domain.model.valueobject.InvitationStatus;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "invitations")
public class InvitationJpaEntity {

    @Id
    private UUID id;

    @Column(name = "invited_email", nullable = false)
    private String invitedEmail;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @Column(name = "invited_by", nullable = false)
    private UUID invitedBy;

    @Column(name = "invited_at", nullable = false, updatable = false)
    private Instant invitedAt;

    @Column(name = "responded_at")
    private Instant respondedAt;

    @Column(nullable = false)
    @ColumnTransformer(write = "?::invitation_status")
    private InvitationStatus status;

    protected InvitationJpaEntity() {}

    public InvitationJpaEntity(
        UUID id,
        String invitedEmail,
        UUID organizationId,
        UUID invitedBy,
        Instant invitedAt,
        Instant respondedAt,
        InvitationStatus status
    ) {
        this.id = id;
        this.invitedEmail = invitedEmail;
        this.organizationId = organizationId;
        this.invitedBy = invitedBy;
        this.invitedAt = invitedAt;
        this.respondedAt = respondedAt;
        this.status = status;
    }

    public UUID getId() { return this.id; }
    public String getInvitedEmail() { return this.invitedEmail; }
    public UUID getOrganizationId() { return this.organizationId; }
    public UUID getInvitedBy() { return this.invitedBy; }
    public Instant getInvitedAt() { return this.invitedAt; }
    public Instant getRespondedAt() { return this.respondedAt; }
    public InvitationStatus getStatus() { return this.status; }
}
