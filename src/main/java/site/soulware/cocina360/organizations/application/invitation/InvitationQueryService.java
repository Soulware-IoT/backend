package site.soulware.cocina360.organizations.application.invitation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.organizations.domain.model.aggregate.Invitation;
import site.soulware.cocina360.organizations.domain.model.exception.InvitationNotFoundException;
import site.soulware.cocina360.organizations.domain.model.query.GetInvitationQuery;
import site.soulware.cocina360.organizations.domain.model.query.ListInvitationsByInvitedEmailQuery;
import site.soulware.cocina360.organizations.domain.model.query.ListOrganizationInvitationsQuery;
import site.soulware.cocina360.organizations.domain.model.valueobject.InvitationId;
import site.soulware.cocina360.organizations.domain.repository.InvitationRepository;
import site.soulware.cocina360.profiles.interfaces.acl.ProfileSummary;
import site.soulware.cocina360.profiles.interfaces.acl.ProfilesApi;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class InvitationQueryService {

    private final InvitationRepository invitationRepository;
    private final ProfilesApi profilesApi;

    public InvitationQueryService(
        InvitationRepository invitationRepository,
        ProfilesApi profilesApi
    ) {
        this.invitationRepository = invitationRepository;
        this.profilesApi = profilesApi;
    }

    public InvitationResult handle(GetInvitationQuery query) {
        Invitation invitation = this.invitationRepository.findById(InvitationId.of(query.invitationId()))
                .orElseThrow(() -> InvitationNotFoundException.byId(query.invitationId()));
        UUID invitedById = invitation.getInvitedBy().value();
        ProfileSummary invitedBy = this.profilesApi.findProfiles(List.of(invitedById)).get(invitedById);
        return InvitationResult.from(invitation, invitedBy);
    }

    public List<InvitationResult> handle(ListOrganizationInvitationsQuery query) {
        List<Invitation> invitations =
                this.invitationRepository.findAllByOrganizationId(OrganizationId.of(query.organizationId()));
        List<UUID> inviterIds = invitations.stream().map(i -> i.getInvitedBy().value()).toList();
        Map<UUID, ProfileSummary> profiles = this.profilesApi.findProfiles(inviterIds);
        return invitations.stream()
                .map(i -> InvitationResult.from(i, profiles.get(i.getInvitedBy().value())))
                .toList();
    }

    public List<InvitationResult> handle(ListInvitationsByInvitedEmailQuery query) {
        List<Invitation> invitations =
                this.invitationRepository.findAllByInvitedEmail(query.invitedEmail());
        List<UUID> inviterIds = invitations.stream().map(i -> i.getInvitedBy().value()).toList();
        Map<UUID, ProfileSummary> profiles = this.profilesApi.findProfiles(inviterIds);
        return invitations.stream()
                .map(i -> InvitationResult.from(i, profiles.get(i.getInvitedBy().value())))
                .toList();
    }
}
