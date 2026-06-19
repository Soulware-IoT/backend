package site.soulware.cocina360.organizations.interfaces.acl;

import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;

import java.util.UUID;

/**
 * Anti-corruption layer port of the {@code organizations} context (Spring Modulith named
 * interface {@code "acl"}).
 * <p>
 * Lets other bounded contexts verify the existence of an {@code Organization} without
 * importing any of the {@code organizations} module's internal packages. Signatures use only
 * shared value objects or primitives, and existence is enforced by reusing the canonical
 * {@code OrganizationNotFoundException} thrown by the organizations query service.
 */
public interface OrganizationsApi {

    /**
     * Verifies an organization with the given id exists and returns its typed id.
     *
     * @throws site.soulware.cocina360.organizations.domain.model.exception.OrganizationNotFoundException if absent
     */
    OrganizationId requireOrganizationId(UUID organizationId);
}
