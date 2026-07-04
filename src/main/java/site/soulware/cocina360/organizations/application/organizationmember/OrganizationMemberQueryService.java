package site.soulware.cocina360.organizations.application.organizationmember;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.organizations.domain.model.aggregate.OrganizationMember;
import site.soulware.cocina360.organizations.domain.model.exception.OrganizationMemberNotFoundException;
import site.soulware.cocina360.organizations.domain.model.query.GetOrganizationMemberQuery;
import site.soulware.cocina360.organizations.domain.model.query.GetOrganizationMembershipQuery;
import site.soulware.cocina360.organizations.domain.model.query.ListOrganizationMembersQuery;
import site.soulware.cocina360.organizations.domain.model.valueobject.OrganizationMemberId;
import site.soulware.cocina360.organizations.domain.repository.OrganizationMemberRepository;
import site.soulware.cocina360.profiles.interfaces.acl.ProfileSummary;
import site.soulware.cocina360.profiles.interfaces.acl.ProfilesApi;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class OrganizationMemberQueryService {

    private final OrganizationMemberRepository memberRepository;
    private final ProfilesApi profilesApi;

    public OrganizationMemberQueryService(
        OrganizationMemberRepository memberRepository,
        ProfilesApi profilesApi
    ) {
        this.memberRepository = memberRepository;
        this.profilesApi = profilesApi;
    }

    public OrganizationMemberResult handle(GetOrganizationMemberQuery query) {
        OrganizationMember member = this.memberRepository.findById(OrganizationMemberId.of(query.memberId()))
                .orElseThrow(() -> OrganizationMemberNotFoundException.byId(query.memberId()));
        UUID profileId = member.getProfileId().value();
        ProfileSummary profile = this.profilesApi.findProfiles(List.of(profileId)).get(profileId);
        return OrganizationMemberResult.from(member, profile);
    }

    public OrganizationMemberResult handle(GetOrganizationMembershipQuery query) {
        OrganizationMember member = this.memberRepository
                .findByOrganizationIdAndProfileId(
                        OrganizationId.of(query.organizationId()), ProfileId.of(query.profileId()))
                .orElseThrow(() -> OrganizationMemberNotFoundException
                        .byProfileInOrganization(query.organizationId(), query.profileId()));
        UUID profileId = member.getProfileId().value();
        ProfileSummary profile = this.profilesApi.findProfiles(List.of(profileId)).get(profileId);
        return OrganizationMemberResult.from(member, profile);
    }

    public List<OrganizationMemberResult> handle(ListOrganizationMembersQuery query) {
        List<OrganizationMember> members =
                this.memberRepository.findAllByOrganizationId(OrganizationId.of(query.organizationId()));

        List<UUID> profileIds = members.stream().map(m -> m.getProfileId().value()).toList();
        Map<UUID, ProfileSummary> profiles = this.profilesApi.findProfiles(profileIds);

        return members.stream()
                .map(m -> OrganizationMemberResult.from(m, profiles.get(m.getProfileId().value())))
                .toList();
    }
}
