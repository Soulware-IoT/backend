package site.soulware.cocina360.organizations.application;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.organizations.domain.model.aggregate.Organization;
import site.soulware.cocina360.organizations.domain.model.aggregate.OrganizationMember;
import site.soulware.cocina360.organizations.domain.model.command.CreateOrganizationCommand;
import site.soulware.cocina360.organizations.domain.model.command.DeleteOrganizationCommand;
import site.soulware.cocina360.organizations.domain.model.command.UpdateOrganizationCommand;
import site.soulware.cocina360.organizations.domain.model.exception.OrganizationNotFoundException;
import site.soulware.cocina360.organizations.domain.model.valueobject.Location;
import site.soulware.cocina360.organizations.domain.model.valueobject.OrganizationMemberId;
import site.soulware.cocina360.organizations.domain.repository.OrganizationMemberRepository;
import site.soulware.cocina360.organizations.domain.repository.OrganizationRepository;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;

@Service
@Transactional
public class OrganizationCommandService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository memberRepository;
    private final ApplicationEventPublisher eventPublisher;

    public OrganizationCommandService(
        OrganizationRepository organizationRepository,
        OrganizationMemberRepository memberRepository,
        ApplicationEventPublisher eventPublisher
    ) {
        this.organizationRepository = organizationRepository;
        this.memberRepository = memberRepository;
        this.eventPublisher = eventPublisher;
    }

    public void handle(CreateOrganizationCommand command) {
        OrganizationId id = OrganizationId.of(command.organizationId());
        ProfileId requesterId = ProfileId.of(command.requesterId());
        Location location = this.toLocation(command.latitude(), command.longitude());

        Organization org = Organization.create(id, command.name(), command.imageUrl(),
                command.addressLineOne(), command.addressLineTwo(), command.addressReference(),
                location, requesterId, requesterId);

        this.organizationRepository.save(org);
        org.pullDomainEvents().forEach(this.eventPublisher::publishEvent);

        OrganizationMember owner = OrganizationMember.createOwner(OrganizationMemberId.generate(), requesterId, id);
        this.memberRepository.save(owner);
        owner.pullDomainEvents().forEach(this.eventPublisher::publishEvent);
    }

    public void handle(UpdateOrganizationCommand command) {
        Organization org = this.findOrThrow(OrganizationId.of(command.organizationId()));
        Location location = this.toLocation(command.latitude(), command.longitude());

        org.update(command.name(), command.imageUrl(), command.addressLineOne(),
                command.addressLineTwo(), command.addressReference(), location,
                ProfileId.of(command.requesterId()));

        this.organizationRepository.save(org);
        org.pullDomainEvents().forEach(this.eventPublisher::publishEvent);
    }

    public void handle(DeleteOrganizationCommand command) {
        Organization org = this.findOrThrow(OrganizationId.of(command.organizationId()));
        this.organizationRepository.delete(org);
    }

    private Organization findOrThrow(OrganizationId id) {
        return this.organizationRepository.findById(id)
                .orElseThrow(() -> OrganizationNotFoundException.byId(id.value()));
    }

    private Location toLocation(Double latitude, Double longitude) {
        return (latitude != null && longitude != null) ? new Location(latitude, longitude) : null;
    }
}
