# AGENTS.md

# Project

This repository contains an Android 2D asynchronous loot RPG.

The primary gameplay inspiration is the interaction model of MouseHunt:
the player periodically performs a hunt rather than controlling real-time combat.

The progression and loot philosophy is inspired by loot-driven ARPGs such as
Diablo and Path of Exile.

Do NOT copy copyrighted assets, text, characters, item names, monsters,
UI designs, or proprietary game content from existing games.

## Core Gameplay

The basic gameplay loop is:

Player -> Select Area -> Hunt -> Monster Encounter -> Combat Resolution
-> Rewards -> Loot -> Equipment -> Character Progression -> Harder Areas

A player can normally perform one hunt every 15 minutes.

The initial version is completely offline.

---

# Engineering Standard

Treat this as a production-quality application intended to grow for years.

Use engineering practices expected from a mature large-scale software team.

Prioritize:

- maintainability
- testability
- clear separation of concerns
- modularity
- predictable state management
- type safety
- explicit domain models
- dependency inversion
- small focused classes/functions
- readable code over clever code
- comprehensive tests for game rules

Do not over-engineer functionality that does not yet exist.

Architecture should support future scale without implementing future
infrastructure prematurely.

---

# Technology

Use:

- Kotlin
- Jetpack Compose
- Material 3
- Coroutines
- Flow / StateFlow
- ViewModel
- Room for local persistence
- Gradle Kotlin DSL
- JUnit for unit testing

Prefer official Android/Jetpack libraries.

Do not introduce a new third-party dependency unless it provides substantial
value and cannot reasonably be implemented using the existing stack.

---

# Architecture

Use layered architecture with:

UI
↓
Presentation
↓
Domain
↓
Data

The Domain layer MUST NOT depend on Android UI, Room, networking,
Supabase, or any specific persistence technology.

Business/game rules belong in the Domain layer.

Examples:

CalculateCombatUseCase
GenerateLootUseCase
PerformHuntUseCase
CalculateExperienceUseCase

Persistence must be accessed through repository interfaces.

Example:

interface PlayerRepository

interface InventoryRepository

interface HuntRepository

interface AreaRepository

The domain layer depends on these interfaces.

Concrete implementations belong in the data layer.

---

# Critical Future Requirement

THE GAME IS OFFLINE-FIRST TODAY BUT WILL HAVE AN ONLINE BACKEND LATER.

Do not design the application as though local storage will always be the
authoritative source.

Room is currently an implementation detail.

UI and domain code must never directly access Room DAOs.

Use:

UI
↓
ViewModel
↓
Use Case
↓
Repository Interface
↓
Local Repository Implementation
↓
Room

Later we must be able to introduce:

Remote Repository Implementation
↓
Backend/API

without rewriting game logic or UI.

Do NOT implement Supabase, Firebase, REST APIs, authentication or cloud
synchronization yet.

Only create appropriate architectural boundaries for them.

---

# Time

The 15-minute hunt cooldown must NOT be modeled as a countdown that is
the source of truth.

Persist an absolute timestamp such as:

nextHuntAt

The UI may derive a countdown from that timestamp.

Create a TimeProvider/Clock abstraction so game logic does not directly
depend on System.currentTimeMillis().

This makes time logic testable and allows a server-authoritative clock
to replace the local clock in the online version.

For the offline version, LocalTimeProvider may use device time.

---

# Randomness

Random game mechanics must use an injectable abstraction.

Example:

interface RandomProvider

Loot generation and combat must not instantiate Random directly throughout
the codebase.

This enables deterministic unit tests and future server-side resolution.

---

# Game Data

Separate game definitions from player state.

Game definitions include:

- monsters
- areas
- item bases
- item rarities
- affixes
- loot tables
- experience curves

Player state includes:

- character
- level
- XP
- gold
- inventory
- equipment
- unlocked areas
- hunt history
- cooldown state

Do not mix static game configuration with mutable player save data.

---

# UI

Jetpack Compose should primarily render state and emit user actions.

Composable functions must not contain game rules.

Prefer:

Screen
-> ViewModel
-> Use Case
-> Domain

Use immutable UI state objects.

Keep screens small and reusable.

---

# Testing

Game mechanics are high-value test targets.

Unit test:

- combat calculations
- loot probabilities
- rarity selection
- stat calculations
- XP progression
- area unlocking
- cooldown eligibility
- reward calculations

Tests involving randomness must be deterministic.

Do not rely on UI tests to validate domain rules.

---

# Codex Working Rules

Before implementing a significant feature:

1. Read this AGENTS.md.
2. Read relevant files under /docs.
3. Inspect existing architecture.
4. Reuse existing patterns.
5. Avoid creating duplicate abstractions.
6. State any important architectural assumption before changing it.

When implementing:

- Keep changes scoped to the requested milestone.
- Do not implement speculative future features.
- Do not silently change existing game rules.
- Do not bypass architecture for convenience.
- Do not place business logic inside Activities or Composables.
- Do not directly couple domain code to Room.
- Do not introduce networking until explicitly requested.

After implementation:

1. Build the project.
2. Run relevant tests.
3. Fix compilation failures.
4. Fix failing tests caused by the change.
5. Report what was changed.
6. Report tests/build commands executed.
7. Mention architectural decisions or tradeoffs.