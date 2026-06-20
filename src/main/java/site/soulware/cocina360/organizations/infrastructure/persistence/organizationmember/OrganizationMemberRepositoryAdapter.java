package site.soulware.cocina360.organizations.infrastructure.persistence.organizationmember;

import org.springframework.stereotype.Repository;
import site.soulware.cocina360.organizations.domain.model.aggregate.OrganizationMember;
import site.soulware.cocina360.organizations.domain.model.valueobject.InvitationId;
import site.soulware.cocina360.organizations.domain.model.valueobject.OrganizationMemberId;
import site.soulware.cocina360.organizations.domain.model.valueobject.OrganizationMemberPermissions;
import site.soulware.cocina360.organizations.domain.repository.OrganizationMemberRepository;
import site.soulware.cocina360.organizations.infrastructure.persistence.organizationmember.jpa.OrganizationMemberJpaEntity;
import site.soulware.cocina360.organizations.infrastructure.persistence.organizationmember.jpa.OrganizationMemberJpaRepository;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;

import java.util.List;
import java.util.Optional;

@Repository
public class OrganizationMemberRepositoryAdapter implements OrganizationMemberRepository {

    private final OrganizationMemberJpaRepository jpaRepository;

    public OrganizationMemberRepositoryAdapter(OrganizationMemberJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public OrganizationMember save(OrganizationMember aggregate) {
        OrganizationMemberJpaEntity saved = this.jpaRepository.save(this.toJpaEntity(aggregate));
        return this.toDomain(saved);
    }

    @Override
    public Optional<OrganizationMember> findById(OrganizationMemberId id) {
        return this.jpaRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public void delete(OrganizationMember aggregate) {
        this.jpaRepository.deleteById(aggregate.getId().value());
    }

    @Override
    public List<OrganizationMember> findAllByOrganizationId(OrganizationId organizationId) {
        return this.jpaRepository.findAllByOrganizationId(organizationId.value())
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<OrganizationMember> findAllByProfileId(ProfileId profileId) {
        return this.jpaRepository.findAllByProfileId(profileId.value())
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private OrganizationMemberJpaEntity toJpaEntity(OrganizationMember member) {
        OrganizationMemberPermissions perms = member.getPermissions();
        return new OrganizationMemberJpaEntity(
                member.getId().value(),
                member.getProfileId().value(),
                member.getOrganizationId().value(),
                member.getInvitationId() != null ? member.getInvitationId().value() : null,
                member.getJoinedAt(),
                perms.security(),
                perms.iot(),
                perms.internalControl()
        );
    }

    private OrganizationMember toDomain(OrganizationMemberJpaEntity entity) {
        OrganizationMemberPermissions permissions = new OrganizationMemberPermissions(
                entity.getSecurity(),
                entity.getIot(),
                entity.getInternalControl()
        );
        return OrganizationMember.rehydrate(
                OrganizationMemberId.of(entity.getId()),
                ProfileId.of(entity.getProfileId()),
                OrganizationId.of(entity.getOrganizationId()),
                entity.getInvitationId() != null ? InvitationId.of(entity.getInvitationId()) : null,
                entity.getJoinedAt(),
                permissions
        );
    }
}
