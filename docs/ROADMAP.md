# Roadmap

## Milestone 0 — Project Foundation

**Complete — 2026-09-18.** Gradle app, Compose placeholder,
JVM Domain boundary, injectable time/randomness, manual DI, minimal Room integration,
deterministic tests, and repository context documentation.

Build, 10 JVM tests, lint, Room device integration, and an observed app launch all
passed at completion.

## Milestone 1 — Application Shell & Character Foundation

**Complete — 2026-09-18.** Four primary destinations,
dark fantasy shell, persistent default player and basic stats, schema v2 migration,
XP display, equipment/inventory/world placeholders. No gameplay implemented.

Debug build, 15 JVM tests, 3 device tests, lint, and emulator/manual checks passed.
See CURRENT_STATE for supported verification coverage and limitations.

## Directional future milestones (not committed specifications)

1. Milestone 2: agree first hunt-loop scope and rules; shell/profile seams are ready.
2. Monster encounter, automatic combat resolution, and XP/gold rewards.
3. Loot, inventory, and equipment.
4. Progression and persistence expansion.
5. Polish.
6. Backend migration, authority, and account/synchronization design.

Scope and game rules must be agreed per milestone. None of these future features
are implemented in the current shell.
