# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

`cocina360` is a Spring Boot 4.1 backend (Java 25) structured as a **modular monolith** using Spring Modulith, following **Domain-Driven Design (DDD)**. Persistence is backed by **Supabase** (PostgreSQL) used purely as a persistence layer — no Supabase-specific features (auth, realtime, storage) are used from the backend.

## Commands

```bash
# Build
./mvnw clean package

# Run (requires env vars)
DB_URL=jdbc:postgresql://<supabase-host>:5432/postgres \
DB_USERNAME=<user> \
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
├── domain/          ← pure domain: no Spring, no JPA annotations beyond @MappedSuperclass/@Embeddable
│   ├── model/       ← aggregates, entities, value objects
│   ├── event/       ← domain events (past-tense facts)
│   ├── exception/   ← domain rule violations
│   └── repository/  ← repository interfaces (domain contracts, no impl)
├── application/     ← use cases / application services; orchestrates domain, publishes events
└── infrastructure/  ← JPA entities/repos, HTTP controllers, mappers
```

The domain layer has zero infrastructure dependencies. Application services are the only place that coordinate domain objects, repositories, and event publishing.

### Shared Module (`shared/`)

Base classes that all modules reuse, located at `site.soulware.cocina360.shared`:

| Class / Interface | Purpose |
|---|---|
| `shared.domain.model.aggregate.Entity<ID>` | Base entity with identity-based equals/hashCode |
| `shared.domain.model.aggregate.AggregateRoot<ID>` | Extends `Entity`; collects `DomainEvent`s via `registerEvent()`, drained by `pullDomainEvents()` |
| `shared.domain.model.valueobject.ValueObject` | Marker interface; prefer Java `record` + `@Embeddable` for implementations |
| `shared.domain.model.valueobject.AggregateId` | Base class for all UUID-based aggregate identity value objects; extend to create typed IDs |
| `shared.domain.model.event.DomainEvent` | Marker interface for domain events; requires `occurredOn()`. Use `record` types |
| `shared.domain.model.exception.DomainException` | Base unchecked exception for all domain rule violations |
| `shared.domain.model.exception.EntityNotFoundException` | Thrown when an aggregate cannot be found; maps to HTTP 404 |
| `shared.domain.model.exception.BusinessRuleViolationException` | Thrown when a named business rule is violated; maps to HTTP 422 |
| `shared.domain.repository.DomainRepository<A, ID>` | Marker interface for domain repository contracts |

### Domain Events Flow

1. Aggregate calls `registerEvent(new SomethingHappened(...))` during a domain operation.
2. Application service calls `repository.save(aggregate)`.
3. After the transaction, the application service calls `aggregate.pullDomainEvents()` and publishes each event via `ApplicationEventPublisher`.
4. Other modules listen with `@ApplicationModuleListener` (Spring Modulith), ensuring transactional event delivery.

### Security / Auth

Authentication and authorization are **out of scope** for this service. An API gateway sits in front of this monolith and validates JWT tokens issued by Supabase Auth before any request reaches here. This service trusts that incoming requests are already authenticated — do not add Spring Security, JWT parsing, or any auth logic.

### Persistence (Supabase / PostgreSQL)

- Supabase is used exclusively as a hosted PostgreSQL instance — the app connects over JDBC like any PostgreSQL database.
- `ddl-auto=none`: schema is managed externally (migrations expected via Flyway or Supabase dashboard SQL editor).
- JPA/Hibernate is confined to the `infrastructure` layer of each module. Domain objects are mapped via `@MappedSuperclass` from the shared base classes.

## Coding Style

- Always qualify instance field and method accesses with `this.` — this makes the scope of every reference explicit and reduces ambiguity for readers.

## Environment Variables

| Variable | Value format |
|---|---|
| `DB_URL` | `jdbc:postgresql://<host>:5432/postgres` |
| `DB_USERNAME` | Supabase DB user |
| `DB_PASSWORD` | Supabase DB password |
