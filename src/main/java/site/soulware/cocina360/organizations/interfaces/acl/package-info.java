/**
 * Anti-corruption layer of the {@code organizations} context: the published port other
 * bounded contexts use to verify organizations without importing this module's internals.
 * Exposed to Spring Modulith as the named interface {@code "acl"}.
 */
@org.springframework.modulith.NamedInterface("acl")
package site.soulware.cocina360.organizations.interfaces.acl;
