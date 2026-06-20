package site.soulware.cocina360.security.application.reading;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import site.soulware.cocina360.security.domain.model.event.CriticalReadingDetected;

/**
 * Reacts to a {@link CriticalReadingDetected} after the recording transaction commits.
 * The hook for the alerts/notifications capability; for now it logs the safety event.
 *
 * <p>Uses {@code @ApplicationModuleListener} (transactional, async, ordered) so delivery
 * is decoupled from ingestion — a slow or failing alert never blocks or rolls back the
 * reading write.
 */
@Component
class CriticalReadingAlertListener {

    private static final Logger log = LoggerFactory.getLogger(CriticalReadingAlertListener.class);

    @ApplicationModuleListener
    void on(CriticalReadingDetected event) {
        log.warn(
                "CRITICAL reading {} for device {}: {}°C / {} ppm at {}",
                event.readingId(),
                event.deviceId(),
                event.temperatureC(),
                event.gasPpm(),
                event.occurredOn());
    }
}
