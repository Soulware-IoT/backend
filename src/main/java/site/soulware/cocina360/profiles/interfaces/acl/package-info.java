/**
 * Anti-corruption layer of the {@code profiles} context: the published port other bounded
 * contexts use to verify/resolve profiles without importing this module's internals.
 * Exposed to Spring Modulith as the named interface {@code "acl"}.
 */
@org.springframework.modulith.NamedInterface("acl")
package site.soulware.cocina360.profiles.interfaces.acl;
