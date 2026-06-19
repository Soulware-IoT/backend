# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

`cocina360` is a Spring Boot 4.1 backend (Java 25) structured as a **modular monolith** using Spring Modulith, following **Domain-Driven Design (DDD)**. Persistence is backed by **Supabase** (PostgreSQL) used purely as a persistence layer — no Supabase-specific features (auth, realtime, storage) are used from the backend.

## Commands

```bash
# Build
./mvnw clean package

# Run (requires env vars — configure in .vscode/launch.json for VSCode)
DB_URL=jdbc:postgresql://<pooler-host>:5432/postgres?sslmode=require&prepareThreshold=0 \
DB_USERNAME=postgres.<project-ref> \
DB_PASSWORD=<pass> \
./mvnw spring-boot:run

# Run all tests
./mvnw test

# Run a single test class
./mvnw test -Dtest=MyTestClass

# Run a single test method
./mvnw test -Dtest=MyTestClass#myMethod
```

## Architecture

### Modular Monolith (Spring Modulith)

Each top-level subpackage of `site.soulware.cocina360` is an autonomous module with enforced boundaries. Modules must not access each other's internal packages — cross-module communication happens only through **domain events** published on the Spring application event bus, or through explicitly exposed module APIs (classes in the module root package).

### DDD Layering (per module)

Each business module follows this internal layering:

```
<module>/
├── domain/
│   ├── model/
│   │   ├── aggregate/    ← aggregate roots only (extend AggregateRoot<ID> with AggregateId)
│   │   ├── entity/       ← owned entities (extend Entity<ID> with EntityId; no repository, no own events)
│   │   ├── valueobject/  ← value objects (records + @Embeddable)
│   │   ├── event/        ← domain events (past-tense facts, records)
│   │   ├── exception/    ← domain rule violations (extend DomainException)
│   │   ├── command/      ← command records passed to command services
│   │   └── query/        ← query records passed to query services
│   └── repository/       ← repository interfaces (domain contracts, no impl)
├── application/
│   └── <resource>/       ← one sub-package per aggregate/resource; each contains its command service, query service, and Result records
├── infrastructure/
│   └── persistence/
│       └── <resource>/   ← one sub-package per aggregate/resource; each contains its JpaEntity, JpaRepository, RepositoryAdapter, and any converters specific to that resource
└── interfaces/
    └── rest/
        └── <resource>/   ← one sub-package per aggregate/resource; each contains its Controller plus nested request/ and response/ packages
            ├── request/  ← @RequestBody records (validation annotations live here)
            └── response/ ← response records (from(Result) factory)
```

The `application/`, `infrastructure/persistence/`, and `interfaces/rest/` layers are all organized into **one sub-package per aggregate/resource** (e.g. `organization`, `organizationmember`, `invitation`). Every class lives under the resource package it belongs to — no loose classes directly in `application/`, `interfaces/rest/`, etc. The resource names match across all three layers.

Rules:
- The `domain` layer has zero infrastructure or Spring dependencies.
- `application` services are the only place that coordinate domain objects, repositories, and event publishing.
- Command services are `@Transactional`; query services are `@Transactional(readOnly = true)`.
- Controllers call the command service (void), then the query service to return the updated state — two round-trips accepted to keep CQRS boundaries clean.
- HTTP controllers live in `interfaces/rest/<resource>/`, not `infrastructure/`. Infrastructure is for JPA only.

### Aggregate Roots vs Owned Entities

Both live in the `aggregate/` package of their module, but they are structurally distinct:

| | Aggregate Root | Owned Entity |
|---|---|---|
| Extends | `AggregateRoot<ID>` | `Entity<ID>` |
| ID type | extends `AggregateId` | extends `EntityId` |
| Has repository | yes | no — accessed only through its root |
| Can exist alone | yes | no — only meaningful within its aggregate |
| Publishes events | yes | no — mutations are events of the root |

`AggregateId` and `EntityId` are both in `shared.domain.model.valueobject` and share the same UUID-based structure, but using the correct one makes the role of each class explicit.

### Shared Module (`shared/`)

Base classes that all modules reuse, located at `site.soulware.cocina360.shared`:

| Class / Interface | Purpose |
|---|---|
| `shared.domain.model.aggregate.Entity<ID>` | Base entity with identity-based equals/hashCode |
| `shared.domain.model.aggregate.AggregateRoot<ID>` | Extends `Entity`; collects `DomainEvent`s via `registerEvent()`, drained by `pullDomainEvents()` |
| `shared.domain.model.valueobject.ValueObject` | Marker interface; prefer Java `record` + `@Embeddable` for implementations |
| `shared.domain.model.valueobject.AggregateId` | Base class for all UUID-based aggregate identity value objects; extend to create typed IDs |
| `shared.domain.model.event.DomainEvent` | Marker interface for domain events; requires `occurredOn()`. Use `record` types |
| `shared.domain.model.exception.DomainException` | Base unchecked exception; carries `messageKey` + `messageArgs` for i18n resolution |
| `shared.domain.model.exception.EntityNotFoundException` | Thrown when an aggregate cannot be found; maps to HTTP 404 |
| `shared.domain.model.exception.BusinessRuleViolationException` | Thrown when a named business rule is violated; maps to HTTP 422 |
| `shared.domain.repository.DomainRepository<A, ID>` | Marker interface for domain repository contracts |
| `shared.infrastructure.rest.GlobalExceptionHandler` | `@RestControllerAdvice` — maps all exceptions to `ErrorResponse`; resolves messages via `MessageSource` |
| `shared.infrastructure.rest.ErrorResponse` | Standard error envelope: `{ status, error, message, timestamp }` |
| `shared.infrastructure.config.ValidationConfig` | Wires `LocalValidatorFactoryBean` to use the application `MessageSource` |

### Cross-Context ID References

When an aggregate in context A holds a reference to an aggregate that belongs to context B (i.e., stores its ID as a foreign key), that ID type must live in `shared.domain.model.valueobject` — not inside context B's own module. This keeps context A from importing context B's internal packages, preserving module boundary enforcement.

IDs that are only used as a type's own identity (never held as a foreign key by another context) stay inside their own module's `domain.model.valueobject`.

### Cross-Context Existence Checks (Facade Pattern)

Some operations depend on aggregates that live in **another** bounded context. For example, creating an invitation (in `organizations`) requires that a `Profile` with the given email exists (in `profiles`) and that the organization exists. Listing members requires the organization to exist.

To verify those dependencies **without breaking module boundaries**, the owning context exposes a **facade** — an anti-corruption-layer port living in an `interfaces/acl` sub-package, published to Spring Modulith as a **named interface** and implemented by delegating to its own query/command services. Consuming contexts depend only on that port, never on the other module's `application`, `domain`, or `infrastructure` packages.

Rules:
- The facade lives in the owning module's **`interfaces/acl`** package (e.g. `profiles.interfaces.acl.ProfilesApi`). That package's `package-info.java` carries `@org.springframework.modulith.NamedInterface("acl")`, which exposes it as consumable API (sub-packages are otherwise module-internal; only the root package is exposed by default). Both the port and its `@Service` adapter (e.g. `ProfilesApiImpl`) live in this package — the adapter depends on the module's `application` query service (the normal `interfaces → application` direction).
- The facade method signatures use **only shared types** (`shared.domain.model.valueobject.*`) or primitives — never the other module's internal aggregates, `Result` records, or query/command records. A method returns, e.g., a `ProfileId`, not a `Profile` or `ProfileResult`.
- **Existence is enforced by reusing the existing domain exception.** The adapter delegates to the query service, which already throws the canonical `*NotFoundException` (e.g. `ProfileNotFoundException.byEmail(...)`). That exception propagates unchanged up to `GlobalExceptionHandler`, so consumers never reimplement "not found" logic or invent new message keys.
- The consumer calls the facade as a guard, then proceeds. Example: the invitation flow calls `profilesApi.requireProfileId(requesterId)` (throws `ProfileNotFoundException` if absent) and an `organizations`-internal `OrganizationQueryService` lookup for the organization, before creating the invitation.

```java
// profiles/interfaces/acl/package-info.java
@org.springframework.modulith.NamedInterface("acl")
package site.soulware.cocina360.profiles.interfaces.acl;

// profiles/interfaces/acl/ProfilesApi.java — published ACL port
public interface ProfilesApi {
    ProfileId requireProfileId(UUID profileId);        // throws ProfileNotFoundException if absent
    ProfileId requireProfileIdByEmail(String email);  // throws ProfileNotFoundException if absent
}

// profiles/interfaces/acl/ProfilesApiImpl.java — adapter, delegates + reuses domain exception
@Service
class ProfilesApiImpl implements ProfilesApi {
    private final ProfileQueryService queryService;
    // ...
    public ProfileId requireProfileId(UUID profileId) {
        return ProfileId.of(this.queryService.handle(new GetProfileQuery(profileId)).profileId());
    }
}
```

Prefer this synchronous facade for **read/verification** dependencies that must resolve before a command completes. Continue to use **domain events** (`@ApplicationModuleListener`) for fire-and-forget reactions after a fact has occurred — facades are not a replacement for the event-driven flow.

### Same-Context Existence Checks

The same robustness rule applies to references **within** a single bounded context. Before a controller dispatches a command that targets or references an existing aggregate/entity of its own context, it **verifies existence through the context's own query service** — the query service already throws the canonical `*NotFoundException` when the resource is absent.

- The check is a `queryService.handle(GetXQuery)` call; its `*NotFoundException` propagates to `GlobalExceptionHandler`, so the endpoint returns a correct 404 without the controller writing any conditional/exception logic.
- This fits the established CQRS flow (controller → command service → query service): the verifying query and the post-command read use the **same** query service, reusing the same exceptions and message keys.
- The result: every endpoint that names a referenced resource is robust by construction — a missing aggregate/entity surfaces as the existing domain exception rather than a downstream constraint error or a `NullPointerException`.

In short: **cross-context references are guarded via the owning module's facade; same-context references are guarded via the context's own query service.** Both reuse existing domain exceptions instead of reimplementing not-found handling.

### Exception Conventions

- All domain exceptions extend `DomainException` and pass a **message key** (not a hardcoded string) to the super constructor, e.g. `super("error.profile.not_found_by_id", id)`.
- Each bounded context defines its own specific exception classes with the key burned in — application services never pass raw string keys.
- `GlobalExceptionHandler` resolves the key via `MessageSource` + `LocaleContextHolder.getLocale()` to produce a translated message.

### i18n

- The frontend (and Postman) sets the `Accept-Language` header (`en` or `es`).
- Spring's `AcceptHeaderLocaleResolver` picks up the locale automatically; default falls back to `en` (`spring.mvc.locale=en`).
- All user-facing messages live in `src/main/resources/messages.properties` and `messages_es.properties`, organized by bounded context using comment sections.
- Bean Validation constraint messages also resolve from these files (via `ValidationConfig`), using standard keys like `jakarta.validation.constraints.NotBlank.message`.

### Domain Events Flow

1. Aggregate calls `registerEvent(new SomethingHappened(...))` during a domain operation.
2. Application service calls `repository.save(aggregate)`.
3. After the transaction, the application service calls `aggregate.pullDomainEvents()` and publishes each event via `ApplicationEventPublisher`.
4. Other modules listen with `@ApplicationModuleListener` (Spring Modulith), ensuring transactional event delivery.

### Security / Auth

Authentication and authorization are **out of scope** for this service. An API gateway sits in front of this monolith and validates JWT tokens issued by Supabase Auth before any request reaches here. This service trusts that incoming requests are already authenticated — do not add Spring Security, JWT parsing, or any auth logic.

### Persistence (Supabase / PostgreSQL)

- Supabase is used exclusively as a hosted PostgreSQL instance — the app connects over JDBC like any PostgreSQL database.
- Connect via the **Supabase connection pooler** (PgBouncer), not the direct connection, to avoid IPv6 routing issues. Use `?sslmode=require&prepareThreshold=0` in the JDBC URL (`prepareThreshold=0` disables prepared statements, required for PgBouncer in transaction mode).
- `ddl-auto=none`: schema is managed externally (migrations via Flyway or Supabase dashboard SQL editor).
- JPA/Hibernate is confined to the `infrastructure` layer of each module. Domain objects are mapped via `@MappedSuperclass` from the shared base classes.

#### PostgreSQL enum columns

The database defines several native `enum` types (e.g. `permission_level`, `invitation_status`). The PostgreSQL JDBC driver always sends `String`-converted values as `varchar`, and PostgreSQL **never** implicitly casts `varchar → <named enum>`, so a bare insert fails with `column "x" is of type <enum> but expression is of type character varying`.

**Convention:** map the field to a Java enum with an `AttributeConverter` (producing the lowercase label) **and** add an explicit write-cast so Hibernate emits the cast in the generated SQL:

```java
@Column(name = "status", nullable = false)
@ColumnTransformer(write = "?::invitation_status")   // cast varchar bind → native enum
private InvitationStatus status;
```

Apply `@ColumnTransformer(write = "?::<pg_enum_type>")` to **every** column backed by a PostgreSQL native enum. This keeps the fix scoped per-column (preferred over the global `stringtype=unspecified` JDBC flag).

#### Database triggers / RLS are out of scope

The DB is treated purely as persistence. Supabase-Auth-oriented artifacts — `BEFORE INSERT` triggers that set audit columns from `auth.uid()`, auto-membership triggers, and Row-Level Security — are **incompatible** with this backend: it connects as the pooler role with no Supabase auth session, so `auth.uid()` is `NULL` and silently nulls audit columns (`created_by`, `owned_by`, …), causing `NOT NULL` violations even when the app sends valid values. The **application is the source of truth** for audit fields and derived rows; such triggers/RLS must be dropped on tables this service writes to.

## Coding Style

- Always qualify instance field and method accesses with `this.` — this makes the scope of every reference explicit and reduces ambiguity for readers.
- When a method declaration's parameter list is too long to fit on one line, break each parameter onto its own line, indented one tab beyond the method's access modifier. The closing `)` and opening `{` go on their own line at the access modifier's indentation level:

```java
public static ControlFormat rehydrate(
    ControlFormatId id,
    ControlProcessId processId,
    String name,
    ControlFormatStatus status,
    List<FormatField> fields,
    Instant createdAt,
    Instant updatedAt
) {
    // body
}
```

## Environment Variables

| Variable | Value format |
|---|---|
| `DB_URL` | `jdbc:postgresql://<pooler-host>:5432/postgres?sslmode=require&prepareThreshold=0` |
| `DB_USERNAME` | `postgres.<project-ref>` (pooler format) |
| `DB_PASSWORD` | Supabase DB password |
