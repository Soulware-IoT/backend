package site.soulware.cocina360.organizations.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.organizations.domain.model.exception.InvitationNotFoundException;
import site.soulware.cocina360.organizations.domain.model.query.GetInvitationQuery;
import site.soulware.cocina360.organizations.domain.model.query.ListOrganizationInvitationsQuery;
import site.soulware.cocina360.organizations.domain.model.valueobject.InvitationId;
import site.soulware.cocina360.organizations.domain.repository.InvitationRepository;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class InvitationQueryService {

    private final InvitationRepository invitationRepository;

    public InvitationQueryService(InvitationRepository invitationRepository) {
        this.invitationRepository = invitationRepository;
    }

    public InvitationResult handle(GetInvitationQuery query) {
        return this.invitationRepository.findById(InvitationId.of(query.invitationId()))
                .map(InvitationResult::from)
                .orElseThrow(() -> InvitationNotFoundException.byId(query.invitationId()));
    }

    public List<InvitationResult> handle(ListOrganizationInvitationsQuery query) {
        return this.invitationRepository.findAllByOrganizationId(OrganizationId.of(query.organizationId()))
                .stream().map(InvitationResult::from).toList();
    }
}
