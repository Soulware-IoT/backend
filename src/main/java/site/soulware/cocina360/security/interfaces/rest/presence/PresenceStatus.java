package site.soulware.cocina360.security.interfaces.rest.presence;

/**
 * Live reachability of a device, derived from how recently it (or, for an IoT device,
 * the edge relaying for it) was last heard from — not a persisted business fact.
 */
public enum PresenceStatus {
    ONLINE,
    OFFLINE
}
