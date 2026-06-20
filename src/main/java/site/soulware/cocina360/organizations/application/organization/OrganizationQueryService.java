package site.soulware.cocina360.organizations.application.organization;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.organizations.domain.model.aggregate.OrganizationMember;
import site.soulware.cocina360.organizations.domain.model.exception.OrganizationNotFoundException;
import site.soulware.cocina360.organizations.domain.model.query.GetOrganizationQuery;
import site.soulware.cocina360.organizations.domain.model.query.ListOrganizationsByProfileQuery;
import site.soulware.cocina360.organizations.domain.repository.OrganizationMemberRepository;
import site.soulware.cocina360.organizations.domain.repository.OrganizationRepository;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;
import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrganizationQueryService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationMemberRepository organizationMemberRepository;

    public OrganizationQueryService(
        OrganizationRepository organizationRepository,
        OrganizationMemberRepository organizationMemberRepository
    ) {
        this.organizationRepository = organizationRepository;
        this.organizationMemberRepository = organizationMemberRepository;
    }

    public OrganizationResult handle(GetOrganizationQuery query) {
        return this.organizationRepository.findById(OrganizationId.of(query.organizationId()))
                .map(OrganizationResult::from)
                .orElseThrow(() -> OrganizationNotFoundException.byId(query.organizationId()));
    }

    /**
     * List the organizations a profile belongs to, resolved through its memberships.
     * Memberships referencing a missing organization are skipped rather than erroring.
     */
    public List<OrganizationResult> handle(ListOrganizationsByProfileQuery query) {
        return this.organizationMemberRepository.findAllByProfileId(ProfileId.of(query.profileId())).stream()
                .map(OrganizationMember::getOrganizationId)
                .flatMap(organizationId -> this.organizationRepository.findById(organizationId).stream())
                .map(OrganizationResult::from)
                .toList();
    }
}
