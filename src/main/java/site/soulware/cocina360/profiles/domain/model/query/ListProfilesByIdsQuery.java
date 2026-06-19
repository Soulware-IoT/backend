package site.soulware.cocina360.profiles.domain.model.query;

import java.util.Collection;
import java.util.UUID;

public record ListProfilesByIdsQuery(Collection<UUID> profileIds) {
}
