package site.soulware.cocina360.organizations.application.organizationmember;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.organizations.domain.model.aggregate.OrganizationMember;
import site.soulware.cocina360.organizations.domain.model.command.RemoveOrganizationMemberCommand;
import site.soulware.cocina360.organizations.domain.model.command.UpdateMemberPermissionsCommand;
import site.soulware.cocina360.organizations.domain.model.exception.OrganizationMemberNotFoundException;
import site.soulware.cocina360.organizations.domain.model.valueobject.OrganizationMemberId;
import site.soulware.cocina360.organizations.domain.model.valueobject.OrganizationMemberPermissions;
import site.soulware.cocina360.organizations.domain.repository.OrganizationMemberRepository;

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

        member.updatePermissions(new OrganizationMemberPermissions(
                command.security(), command.iot(), command.internalControl()));

        this.memberRepository.save(member);
        member.pullDomainEvents().forEach(this.eventPublisher::publishEvent);
    }

    private OrganizationMember findOrThrow(OrganizationMemberId id) {
        return this.memberRepository.findById(id)
                .orElseThrow(() -> OrganizationMemberNotFoundException.byId(id.value()));
    }
}
