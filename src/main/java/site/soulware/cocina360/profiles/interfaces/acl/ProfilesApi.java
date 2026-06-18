package site.soulware.cocina360.profiles.interfaces.acl;

import site.soulware.cocina360.shared.domain.model.valueobject.ProfileId;

import java.util.UUID;

/**
 * Anti-corruption layer port of the {@code profiles} context (Spring Modulith named interface
 * {@code "acl"}).
 * <p>
 * Lets other bounded contexts verify the existence of a {@code Profile} without importing
 * any of the {@code profiles} module's internal packages. Signatures use only shared value
 * objects or primitives, and existence is enforced by reusing the canonical
 * {@code ProfileNotFoundException} thrown by the profiles query service.
 */
public interface ProfilesApi {

    /**
     * Verifies a profile with the given id exists and returns its typed id.
     *
     * @throws site.soulware.cocina360.profiles.domain.model.exception.ProfileNotFoundException if absent
     */
    ProfileId requireProfileId(UUID profileId);

    /**
     * Verifies a profile with the given email exists and returns its typed id.
     *
     * @throws site.soulware.cocina360.profiles.domain.model.exception.ProfileNotFoundException if absent
     */
    ProfileId requireProfileIdByEmail(String email);
}
