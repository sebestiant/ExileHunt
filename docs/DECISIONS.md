# Architecture decision log

## 2026-09-18 — Native Kotlin and Compose, without a game engine

Context: asynchronous, screen-based Android game with no real-time combat.
Decision: Kotlin, Compose/Material 3, ViewModel, Coroutines/StateFlow, Gradle Kotlin
DSL, and JUnit. Rationale: official tooling fits the interaction model. A game
engine would add runtime/build complexity without current value. Consequence:
native screens; revisit rendering only if real requirements demand it.

## 2026-09-18 — One JVM domain module, Android layers in app

Context: Domain must stay independent of Android and persistence. Decision: two
modules; feature-oriented domain packages and layer packages in app. Rationale:
compile-time protection for Domain without dozens of modules. Alternatives: one
module relies entirely on convention; one module per layer adds unnecessary build
overhead now. Consequence: app-internal boundaries require review.

## 2026-09-18 — Offline persistence behind domain interfaces

Context: future server authority, offline prototype today. Decision: Room behind
repository contracts; no network stack. Rationale: keep storage replaceable and
rules portable. Consequence: future APIs must honor domain contracts or evolve
them deliberately; this does not solve synchronization in advance. One temporary
initialization marker validates Room without speculative game entities. Schema
exports are committed; migrations will be required when it is replaced.

## 2026-09-18 — Manual constructor injection

Context: tiny dependency graph. Decision: application-scoped AppContainer and
standard ViewModel factory. Alternatives: Hilt/Koin offer no immediate benefit.
Consequence: explicit, testable wiring; adopt a framework only when complexity
justifies its cost.

## 2026-09-18 — Injectable clock and randomness

Context: future cooldowns, randomized encounters, deterministic tests, and server
authority. Decision: TimeProvider returning Instant and RandomProvider with bounded
integer/unit-double operations; local adapters in Data. Rationale: isolate volatile
dependencies without inventing game rules. Consequence: absolute timestamps will
be persisted; offline clock cheating remains unsolved. API 26 is the minimum to
use java.time directly; supporting older devices would need desugaring and review.

## 2026-09-18 — Stable toolchain and generated Room implementation

Decision: centrally pinned versions in the version catalog; AGP built-in Kotlin,
matching Kotlin JVM/Compose plugins, Gradle wrapper with distribution checksum,
and KSP for Room. KSP is Google's compile-time processor integration, required to
generate type-checked Room implementations; it adds no application runtime framework.
Use JDK 17+ supported by the pinned Gradle version; compile JVM bytecode to 17.
Consequences: builds do not depend on Android Studio. Dependency upgrades must be
verified with build/tests, not assumed compatible from version numbers alone.

Version references checked during setup:
[AGP compatibility](https://developer.android.com/build/releases/agp-9-4-0-release-notes),
[AndroidX releases](https://developer.android.com/jetpack/androidx/versions),
[Compose BOM](https://developer.android.com/develop/ui/compose/bom),
[Kotlin releases](https://kotlinlang.org/docs/releases.html),
[KSP releases](https://github.com/google/ksp/releases),
[Coroutines releases](https://github.com/Kotlin/kotlinx.coroutines/releases).

## 2026-09-18 — One persistent local player

Context: Milestone 1 needs a profile but no accounts or gameplay mutations.
Decision: domain-owned defaults and an atomic getOrCreate repository operation;
Room uses an internal singleton slot distinct from PlayerId. A fixed local ID is
sufficient; a UUID service or account identity would be speculative. Migration
1 -> 2 replaces the temporary marker without destructive fallback. Consequence:
future account support must explicitly map local identity; no unused CRUD APIs.

## 2026-09-18 — Four saveable primary tabs without a navigation stack

Context: Milestone 1 has four peer screens and no nested destinations/deep links.
Decision: Material 3 NavigationBar, saveable selection, per-tab SaveableStateHolder,
and one player ViewModel. Rationale: switching tabs needs no new dependency or
duplicate back-stack entries. Back returns to Hunt. Consequence: navigation must
be revisited when nested flows appear; no generic router is introduced now.
Display/build name becomes ExileHunt, but application ID, database filename, and
internal namespace stay unchanged to preserve existing installations and migration.
