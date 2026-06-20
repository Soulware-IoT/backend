package site.soulware.cocina360.organizations.domain.model.aggregate;

import site.soulware.cocina360.organizations.domain.model.event.MemberPermissionsUpdated;
import site.soulware.cocina360.organizations.domain.model.event.OrganizationMemberJoined;
import site.soulware.cocina360.organizations.domain.model.valueobject.InvitationId;
import site.soulware.cocina360.organizations.domain.model.valueobject.OrganizationMemberId;
import site.soulware.cocina360.organizations.domain.model.valueobject.OrganizationMemberPermissions;
import site.soulware.cocina360.organizations.domain.model.valueobject.PermissionLevel;
import site.soulware.cocina360.shared.domain.model.aggregate.AggregateRoot;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;

import java.time.Instant;

public class OrganizationMember extends AggregateRoot<OrganizationMemberId> {

    private final OrganizationMemberId id;
    private final ProfileId profileId;
    private final OrganizationId organizationId;
    private final InvitationId invitationId;
    private final Instant joinedAt;
    private OrganizationMemberPermissions permissions;

    private OrganizationMember(
        OrganizationMemberId id,
        ProfileId profileId,
        OrganizationId organizationId,
        InvitationId invitationId,
        Instant joinedAt,
        OrganizationMemberPermissions permissions
    ) {
        this.id = id;
        this.profileId = profileId;
        this.organizationId = organizationId;
        this.invitationId = invitationId;
        this.joinedAt = joinedAt;
        this.permissions = permissions;
    }

    public static OrganizationMember create(OrganizationMemberId id, ProfileId profileId,
                                            OrganizationId organizationId, InvitationId invitationId,
                                            OrganizationMemberPermissions permissions) {
        Instant now = Instant.now();
        OrganizationMember member = new OrganizationMember(id, profileId, organizationId, invitationId, now, permissions);
        member.registerEvent(new OrganizationMemberJoined(id.value(), profileId.value(), organizationId.value(), now));
        return member;
    }

    public static OrganizationMember createOwner(OrganizationMemberId id, ProfileId profileId,
                                                  OrganizationId organizationId) {
        OrganizationMemberPermissions adminPermissions = new OrganizationMemberPermissions(
                PermissionLevel.ADMIN, PermissionLevel.ADMIN, PermissionLevel.ADMIN);
        return OrganizationMember.create(id, profileId, organizationId, null, adminPermissions);
    }

    public static OrganizationMember rehydrate(
        OrganizationMemberId id,
        ProfileId profileId,
        OrganizationId organizationId,
        InvitationId invitationId,
        Instant joinedAt,
        OrganizationMemberPermissions permissions
    ) {
        return new OrganizationMember(id, profileId, organizationId, invitationId, joinedAt, permissions);
    }

    public void updatePermissions(OrganizationMemberPermissions permissions) {
        this.permissions = permissions;
        this.registerEvent(new MemberPermissionsUpdated(this.id.value(), this.organizationId.value(), Instant.now()));
    }

    @Override
    public OrganizationMemberId getId() { return this.id; }
    public ProfileId getProfileId() { return this.profileId; }
    public OrganizationId getOrganizationId() { return this.organizationId; }
    public InvitationId getInvitationId() { return this.invitationId; }
    public Instant getJoinedAt() { return this.joinedAt; }
    public OrganizationMemberPermissions getPermissions() { return this.permissions; }
}
