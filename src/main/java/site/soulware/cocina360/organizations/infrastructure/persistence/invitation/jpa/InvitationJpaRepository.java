package site.soulware.cocina360.organizations.infrastructure.persistence.invitation.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InvitationJpaRepository extends JpaRepository<InvitationJpaEntity, UUID> {

    List<InvitationJpaEntity> findAllByOrganizationId(UUID organizationId);

    List<InvitationJpaEntity> findAllByInvitedEmail(String invitedEmail);
}
