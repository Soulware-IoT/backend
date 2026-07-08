package site.soulware.cocina360.organizations.application.organizationmember;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.organizations.domain.model.aggregate.OrganizationMember;
import site.soulware.cocina360.organizations.domain.model.command.RemoveOrganizationMemberCommand;
import site.soulware.cocina360.organizations.domain.model.command.UpdateMemberPermissionsCommand;
import site.soulware.cocina360.organizations.domain.model.exception.InsufficientPermissionException;
import site.soulware.cocina360.organizations.domain.model.exception.OrganizationMemberNotFoundException;
import site.soulware.cocina360.organizations.domain.model.valueobject.OrganizationMemberId;
import site.soulware.cocina360.organizations.domain.model.valueobject.OrganizationMemberPermissions;
import site.soulware.cocina360.organizations.domain.model.valueobject.PermissionLevel;
import site.soulware.cocina360.organizations.domain.repository.OrganizationMemberRepository;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;

@Service
@Transactional
public class OrganizationMemberCommandService {

    private final OrganizationMemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    public OrganizationMemberCommandService(
        OrganizationMemberRepository memberRepository,
        ApplicationEventPublisher eventPublisher
    ) {
        this.memberRepository = memberRepository;
        this.eventPublisher = eventPublisher;
    }

    public void handle(RemoveOrganizationMemberCommand command) {
        OrganizationMember member = this.findOrThrow(OrganizationMemberId.of(command.memberId()));
        this.memberRepository.delete(member);
    }

    public void handle(UpdateMemberPermissionsCommand command) {
        OrganizationMember member = this.findOrThrow(OrganizationMemberId.of(command.memberId()));

        // The actor may assign only levels strictly below their own organizations-area level.
        PermissionLevel actorLevel = this.actorOrganizationsLevel(command.organizationId(), command.requesterId());
        member.updatePermissions(OrganizationMemberPermissions.assignableBy(actorLevel,
                command.security(), command.organizations(), command.internalControl()));

        this.memberRepository.save(member);
        member.pullDomainEvents().forEach(this.eventPublisher::publishEvent);
    }

    private PermissionLevel actorOrganizationsLevel(java.util.UUID organizationId, java.util.UUID requesterId) {
        return this.memberRepository
                .findByOrganizationIdAndProfileId(OrganizationId.of(organizationId), ProfileId.of(requesterId))
                .orElseThrow(InsufficientPermissionException::new)
                .getPermissions().organizations();
    }

    private OrganizationMember findOrThrow(OrganizationMemberId id) {
        return this.memberRepository.findById(id)
                .orElseThrow(() -> OrganizationMemberNotFoundException.byId(id.value()));
    }
}
