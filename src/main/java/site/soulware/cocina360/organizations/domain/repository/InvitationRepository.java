package site.soulware.cocina360.organizations.domain.repository;

import site.soulware.cocina360.organizations.domain.model.aggregate.Invitation;
import site.soulware.cocina360.organizations.domain.model.valueobject.InvitationId;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.repository.DomainRepository;

import java.util.List;

public interface InvitationRepository extends DomainRepository<Invitation, InvitationId> {

    List<Invitation> findAllByOrganizationId(OrganizationId organizationId);

    List<Invitation> findAllByInvitedEmail(String invitedEmail);
}
