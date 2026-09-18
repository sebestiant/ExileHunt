# Architecture

## Modules and responsibilities

`domain` is a Kotlin/JVM module with no Android or production library dependencies
other than Kotlin's standard library. This small module enforces the most important
boundary at compile time. `app` contains Android-specific layers, separated by
packages rather than additional modules. Namespace: `com.example.lootrpg`.

| Path below source namespace | Responsibility |
| --- | --- |
| `domain/.../core/domain` | TimeProvider and RandomProvider contracts |
| `domain/.../player/domain` | Player/CombatStats models, repository contract, load-or-create use case |
| `app/.../data` | Local adapters and repository implementations |
| `app/.../data/persistence` | Internal Room database, DAO, and entity |
| `app/.../presentation` | HomeViewModel and immutable HomeUiState |
| `app/.../ui` | Stateless Compose rendering |
| `app/.../di` | Application-scoped manual composition root |
| `*/src/test` | JVM tests with deterministic dependencies |
| `app/src/androidTest` | Actual Room integration on Android |

## Runtime calls versus source dependencies

```text
Compose UI
  -> ViewModel (Presentation)
  -> Use case (Domain)
  -> Repository interface (Domain)
  -> Local repository (Data)
  -> Room DAO/database
```

Source dependencies point inward: `app -> domain`; Data implements Domain
interfaces. Domain does not import Data. Room types never cross the repository
boundary. The composition root may know all layers; screens and ViewModels may not.

Future evolution, not implemented:

```text
UI -> Presentation -> Domain -> Repository interfaces
                                  | implementations
                                  +-> Local data -> Room
                                  +-> Remote data -> Backend -> Database
```

Replacing persistence must preserve repository contracts. Server authority will
require deliberate API/error and synchronization design in a future milestone;
no transport or synchronization abstractions are added now. Room is not assumed
to be the permanent authority. Business rules remain independent and portable.

## Current integration slice

HomeViewModel calls LoadPlayerUseCase. Default player values live in Domain.
PlayerRepository.getOrCreate atomically returns the stored profile or persists the
initial player. RoomPlayerRepository maps domain objects to a single internal row;
PlayerDao uses a transaction and conflict-ignore insertion to avoid resets/races.
Migration 1 -> 2 replaces the temporary marker with the player table. The former
marker is not player data; its schema export remains available for migration tests.

Room runs suspend DAO work off the main thread. The database is application-scoped,
exports schemas, and has no destructive fallback. AppContainer owns concrete
dependencies; Activities obtain ViewModels via the standard factory. Other classes
use constructor injection, not a service locator. No DI framework is needed.

## Time and randomness

TimeProvider returns `java.time.Instant`. LocalTimeProvider delegates to an
injectable `java.time.Clock` (UTC system clock by default). Min SDK 26 provides
java.time without desugaring. Future cooldown state will use absolute timestamps;
UI countdowns will be derived. Offline device time is mutable and not trusted
against clock manipulation; a server-backed clock can replace this adapter later.

RandomProvider offers bounded integers and unit-interval doubles. LocalRandomProvider
wraps Kotlin Random at this single boundary. Tests can inject scripted fakes or
seeded generators. No probability rules, loot, or combat are implemented. The
production RNG is wired but intentionally unused until gameplay requires it.

## State and error conventions

ViewModels expose read-only StateFlow of immutable UI state. Compose uses lifecycle-
aware collection and emits actions. Home initialization has Loading, Loaded, and
Error states; retry is guarded against duplicate active work. Coroutine cancellation
is rethrown. Data failures propagate to the presentation boundary, which displays a
generic retry message. Add typed domain failures only when concrete game operations
require distinctions. Presentation and UI imports are reviewed for layer violations;
only the Domain boundary is enforced by a separate module today.

ExperienceDisplay supplies the specified Level 1 target (100 XP) and a bounded
display fraction. Other level requirements remain unspecified; this is not an XP
curve or a leveling system. No model mutation operations exist yet.
