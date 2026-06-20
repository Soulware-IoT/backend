package site.soulware.cocina360.organizations.domain.model.query;

import java.util.UUID;

/** List the organizations a profile belongs to (via its memberships). */
public record ListOrganizationsByProfileQuery(UUID profileId) {}
