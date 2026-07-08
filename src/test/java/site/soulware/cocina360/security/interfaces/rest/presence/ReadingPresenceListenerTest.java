package site.soulware.cocina360.security.interfaces.rest.presence;

import org.junit.jupiter.api.Test;

import site.soulware.cocina360.security.domain.model.event.ReadingRecorded;
import site.soulware.cocina360.security.domain.model.valueobject.SafetySeverity;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

class ReadingPresenceListenerTest {

    private final UUID organizationId = UUID.randomUUID();
    private final UUID deviceId = UUID.randomUUID();

    @Test
    void freshReadingTouchesTheDevicePresence() {
        DevicePresenceRegistry registry = mock(DevicePresenceRegistry.class);
        ReadingPresenceListener listener = new ReadingPresenceListener(registry);

        listener.on(this.reading(Instant.now()));

        verify(registry).touch(this.organizationId, this.deviceId, "DEV-1", DeviceKind.IOT);
    }

    @Test
    void staleReadingFromABacklogFlushIsIgnored() {
        DevicePresenceRegistry registry = mock(DevicePresenceRegistry.class);
        ReadingPresenceListener listener = new ReadingPresenceListener(registry);

        listener.on(this.reading(Instant.now().minus(5, ChronoUnit.MINUTES)));

        verifyNoInteractions(registry);
    }

    private ReadingRecorded reading(Instant occurredAt) {
        return new ReadingRecorded(
                this.organizationId, UUID.randomUUID(), this.deviceId, "DEV-1",
                45, 12.5, SafetySeverity.SAFE, occurredAt, Instant.now());
    }
}
