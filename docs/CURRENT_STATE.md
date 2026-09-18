# Current state

- Milestone: **2 — The First Hunt**, in progress.
- App: ExileHunt; existing ID/namespace `com.example.lootrpg` retained; min API 26.
- Implemented: persistent Player/CombatStats domain models, atomic load-or-create
  repository, ViewModel loading/loaded/error/retry states, manual DI, time/RNG seams.
  Room v2 replaces the temporary marker via explicit migration. Four saveable tabs:
  Hunt, Character, Inventory, World; dark theme, XP display, scrollable placeholders.
- Architecture: JVM-only `domain` module; `app` packages separate UI, presentation,
  data/persistence, and dependency wiring. No gameplay or networking.
- Key paths: `domain/src/main/kotlin`, `app/src/main/kotlin`, `app/schemas`,
  `gradle/libs.versions.toml`; unit tests in each module's `src/test`, Room test in
  `app/src/androidTest`.
- Verified: debug build/install, 15 JVM tests, 3 Room device tests, and lint (no issues).
  Device tests cover creation/concurrent loads, reopening existing data, and v1 -> v2.
  API 37 emulator: all tabs, Hunt message only, player restart, scroll/tab restoration
  after confirmed process death, Back to Hunt, and 320dp width at 130% text size.
  Commands/prerequisites: README. API 26 and physical devices not tested.
- Tooling warnings: JDK 25 exposes an upstream protobuf Unsafe deprecation during
  device testing; an AndroidX native library is packaged without symbol stripping.
  Neither prevented validation. No dependency downgrade or extra NDK was needed.
- Limitations: one offline profile, no player mutation APIs; only Level 1 XP target
  specified; no release signing or backend. Device time remains mutable.
- Domain hunt rules, weighted definitions, combat, and progression now implemented
  and JVM-tested; Room v3 transactional persistence implemented, UI integration next.
- Cooldown configuration: `domain/.../hunt/domain/GameConfig.kt` (10s debug, 15m release).
- Approved limitation: supplied stats make natural defeat impossible; preserve them.

Read ARCHITECTURE for boundaries, GAME_DESIGN for known rules/TBDs, DATA_MODEL for
persisted concepts, DECISIONS for rationale, and ROADMAP for milestone direction.
