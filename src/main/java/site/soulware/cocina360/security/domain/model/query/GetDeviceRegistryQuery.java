package site.soulware.cocina360.security.domain.model.query;

import java.util.UUID;

/**
 * The edge's registry pull: the in-service IoT devices an organization's edge must
 * replicate locally, including each device's {@code device → edge} credential and
 * safety thresholds.
 */
public record GetDeviceRegistryQuery(UUID organizationId) {}
