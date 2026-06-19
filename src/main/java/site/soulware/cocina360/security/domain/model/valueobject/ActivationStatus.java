package site.soulware.cocina360.security.domain.model.valueobject;

/**
 * Lifecycle state shared by registered hardware (edge gateways and devices).
 * An {@code INACTIVE} entity is known to the registry but must not be trusted:
 * the edge stops honouring its credentials and stops accepting its telemetry.
 */
public enum ActivationStatus {
    ACTIVE,
    INACTIVE
}
