# Roadmap

## Milestone 0 - Project Foundation

**Complete - 2026-09-18.** Gradle app, Compose placeholder, JVM domain boundary,
injectable time/randomness, manual DI, minimal Room integration, deterministic
tests, and repository context documentation. Build, 10 JVM tests, lint, Room device
integration, and observed app launch passed at completion.

## Milestone 1 - Application Shell & Character Foundation

**Complete - 2026-09-18.** Four destinations, dark fantasy shell, persistent player
and stats, schema v2 migration, XP display, equipment/inventory/world placeholders.
Debug build, 15 JVM tests, 3 device tests, lint, and emulator checks passed.

## Milestone 2 - The First Hunt

**Complete - 2026-09-18.** Opening lore, five weighted encounters, combat, XP/gold,
leveling, absolute cooldown, transactional saves, recent history, and Character
statistics. No items or backend. Debug/release builds, 31 JVM tests, 9 device tests,
lint, and a ten-minute live playtest passed; see PLAYTEST_M2.md.
The user chose to keep supplied stats despite making natural defeat impossible.

## Directional future milestones (not committed specifications)

1. Milestone 3 scope to be agreed; loot/items remain unimplemented.
2. Inventory and equipment.
3. Progression and persistence expansion.
4. Polish and further balancing.
5. Backend migration, authority, and account/synchronization design.

Scope and game rules must be agreed per milestone. None of these future features
are implemented in the current application.
