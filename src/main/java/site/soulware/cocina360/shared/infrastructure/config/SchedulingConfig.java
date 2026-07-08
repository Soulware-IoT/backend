package site.soulware.cocina360.shared.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Enables {@code @Scheduled} task execution application-wide (e.g. the SSE
 * heartbeat that keeps streaming connections alive and detects dead clients).
 */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
