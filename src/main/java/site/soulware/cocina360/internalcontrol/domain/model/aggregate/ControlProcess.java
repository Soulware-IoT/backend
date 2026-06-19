package site.soulware.cocina360.internalcontrol.domain.model.aggregate;

import site.soulware.cocina360.internalcontrol.domain.model.event.ControlProcessCreated;
import site.soulware.cocina360.internalcontrol.domain.model.valueobject.ControlProcessId;
import site.soulware.cocina360.shared.domain.model.aggregate.AggregateRoot;
import site.soulware.cocina360.shared.domain.model.valueobject.OrganizationId;

import java.time.Instant;
import java.util.Objects;

public class ControlProcess extends AggregateRoot<ControlProcessId> {

    private final ControlProcessId id;
    private final OrganizationId organizationId;
    private String name;
    private final Instant createdAt;
    private Instant updatedAt;

    private ControlProcess(
        ControlProcessId id,
        OrganizationId organizationId,
        String name,
        Instant createdAt,
        Instant updatedAt
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.organizationId = Objects.requireNonNull(organizationId, "organizationId must not be null");
        this.name = requireName(name);
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt must not be null");
    }

    public static ControlProcess create(ControlProcessId id, OrganizationId organizationId, String name) {
        Instant now = Instant.now();
        ControlProcess process = new ControlProcess(id, organizationId, name, now, now);
        process.registerEvent(new ControlProcessCreated(id.value(), name, now));
        return process;
    }

    public static ControlProcess rehydrate(
        ControlProcessId id,
        OrganizationId organizationId,
        String name,                
        Instant createdAt,
        Instant updatedAt
    ) {
        return new ControlProcess(id, organizationId, name, createdAt, updatedAt);
    }

    public void rename(String name) {
        this.name = requireName(name);
        this.updatedAt = Instant.now();
    }

    private static String requireName(String name) {
        if (name == null || name.isBlank()) throw new IllegalArgumentException("name must not be blank");
        return name.trim();
    }

    @Override
    public ControlProcessId getId() { return this.id; }
    public OrganizationId getOrganizationId() { return this.organizationId; }
    public String getName() { return this.name; }
    public Instant getCreatedAt() { return this.createdAt; }
    public Instant getUpdatedAt() { return this.updatedAt; }
}
