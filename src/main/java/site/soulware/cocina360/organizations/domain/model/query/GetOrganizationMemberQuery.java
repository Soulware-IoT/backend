package site.soulware.cocina360.organizations.domain.model.query;

import java.util.UUID;

public record GetOrganizationMemberQuery(UUID organizationId, UUID memberId) {}
