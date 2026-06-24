package site.soulware.cocina360.profiles.interfaces.acl;

import java.util.UUID;

/**
 * Published read model of a {@code Profile} exposed by the {@code profiles} context through its
 * ACL named interface. Other bounded contexts embed this view into their own results (e.g. an
 * organization member listing) without importing the {@code profiles} module's internals.
 */
public record ProfileSummary(
        UUID id,
        String fullName,
        String preferredName,
        String email,
        String avatarUrl
) {
}
