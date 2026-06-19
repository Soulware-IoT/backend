package site.soulware.cocina360.organizations.application.organizationmember;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.soulware.cocina360.organizations.domain.model.exception.OrganizationMemberNotFoundException;
import site.soulware.cocina360.organizations.domain.model.query.GetOrganizationMemberQuery;
import site.soulware.cocina360.organizations.domain.model.query.ListOrganizationMembersQuery;
import site.soulware.cocina360.organizations.domain.model.valueobject.OrganizationMemberId;
import site.soulware.cocina360.organizations.domain.repository.OrganizationMemberRepository;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrganizationMemberQueryService {

    private final OrganizationMemberRepository memberRepository;

    public OrganizationMemberQueryService(OrganizationMemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public OrganizationMemberResult handle(GetOrganizationMemberQuery query) {
        return this.memberRepository.findById(OrganizationMemberId.of(query.memberId()))
                .map(OrganizationMemberResult::from)
                .orElseThrow(() -> OrganizationMemberNotFoundException.byId(query.memberId()));
    }

    public List<OrganizationMemberResult> handle(ListOrganizationMembersQuery query) {
        return this.memberRepository.findAllByOrganizationId(OrganizationId.of(query.organizationId()))
                .stream().map(OrganizationMemberResult::from).toList();
    }
}
