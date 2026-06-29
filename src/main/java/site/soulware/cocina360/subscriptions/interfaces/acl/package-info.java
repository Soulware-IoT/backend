/**
 * Anti-corruption layer of the {@code subscriptions} context: the published port other
 * bounded contexts use to resolve a subscription's device quota without importing this
 * module's internals. Exposed to Spring Modulith as the named interface {@code "acl"}.
 */
@org.springframework.modulith.NamedInterface("acl")
package site.soulware.cocina360.subscriptions.interfaces.acl;
