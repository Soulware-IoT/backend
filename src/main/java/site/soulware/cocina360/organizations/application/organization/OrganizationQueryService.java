package site.soulware.cocina360.organizations.application.organization;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.organizations.domain.model.aggregate.Organization;
import site.soulware.cocina360.organizations.domain.model.aggregate.OrganizationMember;
import site.soulware.cocina360.organizations.domain.model.exception.OrganizationNotFoundException;
import site.soulware.cocina360.organizations.domain.model.query.GetOrganizationQuery;
import site.soulware.cocina360.organizations.domain.model.query.ListOrganizationsByProfileQuery;
import site.soulware.cocina360.organizations.domain.repository.OrganizationMemberRepository;
import site.soulware.cocina360.organizations.domain.repository.OrganizationRepository;
import site.soulware.cocina360.profiles.interfaces.acl.ProfileSummary;
import site.soulware.cocina360.profiles.interfaces.acl.ProfilesApi;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class OrganizationQueryService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;
    private final ProfilesApi profilesApi;

    public OrganizationQueryService(
        OrganizationRepository organizationRepository,
        OrganizationMemberRepository organizationMemberRepository,
        ProfilesApi profilesApi
    ) {
        this.organizationRepository = organizationRepository;
        this.organizationMemberRepository = organizationMemberRepository;
        this.profilesApi = profilesApi;
    }

    public OrganizationResult handle(GetOrganizationQuery query) {
        Organization organization = this.organizationRepository.findById(OrganizationId.of(query.organizationId()))
                .orElseThrow(() -> OrganizationNotFoundException.byId(query.organizationId()));
        UUID ownerId = organization.getOwnedBy().value();
        ProfileSummary owner = this.profilesApi.findProfiles(List.of(ownerId)).get(ownerId);
        return OrganizationResult.from(organization, owner);
    }

    /**
     * List the organizations a profile belongs to, resolved through its memberships.
     * Memberships referencing a missing organization are skipped rather than erroring.
     */
    public List<OrganizationResult> handle(ListOrganizationsByProfileQuery query) {
        List<Organization> organizations =
                this.organizationMemberRepository.findAllByProfileId(ProfileId.of(query.profileId())).stream()
                        .map(OrganizationMember::getOrganizationId)
                        .flatMap(organizationId -> this.organizationRepository.findById(organizationId).stream())
                        .toList();

        List<UUID> ownerIds = organizations.stream().map(o -> o.getOwnedBy().value()).toList();
        Map<UUID, ProfileSummary> owners = this.profilesApi.findProfiles(ownerIds);

        return organizations.stream()
                .map(o -> OrganizationResult.from(o, owners.get(o.getOwnedBy().value())))
                .toList();
    }
}
