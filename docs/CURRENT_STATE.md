# Current state

- Milestone: **1 — Application Shell & Character Foundation**, in progress.
- App: LootRPG (temporary), `com.example.lootrpg`; minimum Android API 26.
- Implemented: persistent Player/CombatStats domain models, atomic load-or-create
  repository, ViewModel loading/loaded/error/retry states, manual DI, time/RNG seams.
  Room v2 replaces the temporary marker via explicit migration. Shell UI is pending.
- Architecture: JVM-only `domain` module; `app` packages separate UI, presentation,
  data/persistence, and dependency wiring. No gameplay or networking.
- Key paths: `domain/src/main/kotlin`, `app/src/main/kotlin`, `app/schemas`,
  `gradle/libs.versions.toml`; unit tests in each module's `src/test`, Room test in
  `app/src/androidTest`.
- Player foundation build, 15 JVM tests, and 3 device tests pass (first creation,
  concurrent loads, persistence across reopening, and v1-to-v2 migration).
  Commands/prerequisites: README. API 26 not device-tested.
- Tooling warnings: JDK 25 exposes an upstream protobuf Unsafe deprecation during
  device testing; an AndroidX native library is packaged without symbol stripping.
  Neither prevented validation. No dependency downgrade or extra NDK was needed.
- Limitations: one offline profile, no player mutation APIs; only Level 1 XP target
  specified; no release signing or backend. Device time remains mutable.
- Next: complete and verify Milestone 1 shell. Milestone 2 is not started.

Read ARCHITECTURE for boundaries, GAME_DESIGN for known rules/TBDs, DATA_MODEL for
persisted concepts, DECISIONS for rationale, and ROADMAP for milestone direction.
