package site.soulware.cocina360.organizations.domain.model.aggregate;

import site.soulware.cocina360.organizations.domain.model.event.InvitationAccepted;
import site.soulware.cocina360.organizations.domain.model.event.InvitationDeclined;
import site.soulware.cocina360.organizations.domain.model.event.InvitationSent;
import site.soulware.cocina360.organizations.domain.model.exception.InvitationAlreadyRespondedException;
import site.soulware.cocina360.organizations.domain.model.valueobject.InvitationId;
import site.soulware.cocina360.organizations.domain.model.valueobject.InvitationStatus;
import site.soulware.cocina360.shared.domain.model.aggregate.AggregateRoot;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;

import java.time.Instant;

public class Invitation extends AggregateRoot<InvitationId> {

    private final InvitationId id;
    private final String invitedEmail;
    private final OrganizationId organizationId;
    private final ProfileId invitedBy;
    private final Instant invitedAt;
    private Instant respondedAt;
    private InvitationStatus status;

    private Invitation(
        InvitationId id,
        String invitedEmail,
        OrganizationId organizationId,
        ProfileId invitedBy,
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

    public static Invitation create(
        InvitationId id,
        String invitedEmail,
        OrganizationId organizationId,
        ProfileId invitedBy
    ) {
        Instant now = Instant.now();
        Invitation invitation = new Invitation(id, invitedEmail, organizationId, invitedBy, now, null, InvitationStatus.PENDING);
        invitation.registerEvent(new InvitationSent(id.value(), invitedEmail, organizationId.value(), invitedBy.value(), now));
        return invitation;
    }

    public static Invitation rehydrate(
        InvitationId id,
        String invitedEmail,
        OrganizationId organizationId,
        ProfileId invitedBy,
        Instant invitedAt,
        Instant respondedAt,
        InvitationStatus status
    ) {
        return new Invitation(id, invitedEmail, organizationId, invitedBy, invitedAt, respondedAt, status);
    }

    public void accept() {
        this.requirePending();
        this.status = InvitationStatus.ACCEPTED;
        this.respondedAt = Instant.now();
        this.registerEvent(new InvitationAccepted(this.id.value(), this.organizationId.value(), this.respondedAt));
    }

    public void decline() {
        this.requirePending();
        this.status = InvitationStatus.DECLINED;
        this.respondedAt = Instant.now();
        this.registerEvent(new InvitationDeclined(this.id.value(), this.organizationId.value(), this.respondedAt));
    }

    private void requirePending() {
        if (this.status != InvitationStatus.PENDING) {
            throw new InvitationAlreadyRespondedException(this.id.value());
        }
    }

    @Override
    public InvitationId getId() { return this.id; }
    public String getInvitedEmail() { return this.invitedEmail; }
    public OrganizationId getOrganizationId() { return this.organizationId; }
    public ProfileId getInvitedBy() { return this.invitedBy; }
    public Instant getInvitedAt() { return this.invitedAt; }
    public Instant getRespondedAt() { return this.respondedAt; }
    public InvitationStatus getStatus() { return this.status; }
}
