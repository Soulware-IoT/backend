package site.soulware.cocina360.organizations.application;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.organizations.domain.model.aggregate.Invitation;
import site.soulware.cocina360.organizations.domain.model.aggregate.OrganizationMember;
import site.soulware.cocina360.organizations.domain.model.command.AcceptInvitationCommand;
import site.soulware.cocina360.organizations.domain.model.command.DeclineInvitationCommand;
import site.soulware.cocina360.organizations.domain.model.command.InviteToOrganizationCommand;
import site.soulware.cocina360.organizations.domain.model.exception.InvitationNotFoundException;
import site.soulware.cocina360.organizations.domain.model.valueobject.InvitationId;
import site.soulware.cocina360.organizations.domain.model.valueobject.OrganizationMemberId;
import site.soulware.cocina360.organizations.domain.model.valueobject.PermissionLevel;
import site.soulware.cocina360.organizations.domain.model.valueobject.OrganizationMemberPermissions;
import site.soulware.cocina360.organizations.domain.repository.InvitationRepository;
import site.soulware.cocina360.organizations.domain.repository.OrganizationMemberRepository;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;

@Service
@Transactional
public class InvitationCommandService {

    private final InvitationRepository invitationRepository;
    private final OrganizationMemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    public InvitationCommandService(
        InvitationRepository invitationRepository,
        OrganizationMemberRepository memberRepository,
        ApplicationEventPublisher eventPublisher
    ) {
        this.invitationRepository = invitationRepository;
        this.memberRepository = memberRepository;
        this.eventPublisher = eventPublisher;
    }

    public void handle(InviteToOrganizationCommand command) {
        Invitation invitation = Invitation.create(
                InvitationId.generate(),
                command.invitedEmail(),
                OrganizationId.of(command.organizationId()),
                ProfileId.of(command.invitedBy()));

        this.invitationRepository.save(invitation);
        invitation.pullDomainEvents().forEach(this.eventPublisher::publishEvent);
    }

    public void handle(AcceptInvitationCommand command) {
        Invitation invitation = this.findOrThrow(InvitationId.of(command.invitationId()));
        invitation.accept();

        this.invitationRepository.save(invitation);
        invitation.pullDomainEvents().forEach(this.eventPublisher::publishEvent);

        OrganizationMemberPermissions defaultPermissions = new OrganizationMemberPermissions(
                PermissionLevel.NONE, PermissionLevel.NONE, PermissionLevel.NONE);
        OrganizationMember member = OrganizationMember.create(
                OrganizationMemberId.generate(),
                ProfileId.of(command.acceptingProfileId()),
                invitation.getOrganizationId(),
                invitation.getId(),
                defaultPermissions);

        this.memberRepository.save(member);
        member.pullDomainEvents().forEach(this.eventPublisher::publishEvent);
    }

    public void handle(DeclineInvitationCommand command) {
        Invitation invitation = this.findOrThrow(InvitationId.of(command.invitationId()));
        invitation.decline();

        this.invitationRepository.save(invitation);
        invitation.pullDomainEvents().forEach(this.eventPublisher::publishEvent);
    }

    private Invitation findOrThrow(InvitationId id) {
        return this.invitationRepository.findById(id)
                .orElseThrow(() -> InvitationNotFoundException.byId(id.value()));
    }
}
