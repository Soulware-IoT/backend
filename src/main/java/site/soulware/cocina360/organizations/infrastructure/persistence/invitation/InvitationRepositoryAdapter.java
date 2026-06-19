package site.soulware.cocina360.organizations.infrastructure.persistence.invitation;

import org.springframework.stereotype.Repository;
import site.soulware.cocina360.organizations.domain.model.aggregate.Invitation;
import site.soulware.cocina360.organizations.domain.model.valueobject.InvitationId;
import site.soulware.cocina360.organizations.domain.repository.InvitationRepository;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;

import java.util.List;
import java.util.Optional;

@Repository
public class InvitationRepositoryAdapter implements InvitationRepository {

    private final InvitationJpaRepository jpaRepository;

    public InvitationRepositoryAdapter(InvitationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Invitation save(Invitation aggregate) {
        InvitationJpaEntity saved = this.jpaRepository.save(this.toJpaEntity(aggregate));
        return this.toDomain(saved);
    }

    @Override
    public Optional<Invitation> findById(InvitationId id) {
        return this.jpaRepository.findById(id.value()).map(this::toDomain);
    }

    @Override
    public void delete(Invitation aggregate) {
        this.jpaRepository.deleteById(aggregate.getId().value());
    }

    @Override
    public List<Invitation> findAllByOrganizationId(OrganizationId organizationId) {
        return this.jpaRepository.findAllByOrganizationId(organizationId.value())
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Invitation> findAllByInvitedEmail(String invitedEmail) {
        return this.jpaRepository.findAllByInvitedEmail(invitedEmail)
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private InvitationJpaEntity toJpaEntity(Invitation invitation) {
        return new InvitationJpaEntity(
                invitation.getId().value(),
                invitation.getInvitedEmail(),
                invitation.getOrganizationId().value(),
                invitation.getInvitedBy().value(),
                invitation.getInvitedAt(),
                invitation.getRespondedAt(),
                invitation.getStatus()
        );
    }

    private Invitation toDomain(InvitationJpaEntity entity) {
        return Invitation.rehydrate(
                InvitationId.of(entity.getId()),
                entity.getInvitedEmail(),
                OrganizationId.of(entity.getOrganizationId()),
                ProfileId.of(entity.getInvitedBy()),
                entity.getInvitedAt(),
                entity.getRespondedAt(),
                entity.getStatus()
        );
    }
}
