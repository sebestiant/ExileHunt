# Current state

- **Milestone 2 - The First Hunt: complete (2026-09-18).** ExileHunt is playable
  offline; application ID remains `com.example.lootrpg`, minimum API 26.
- Implemented: acknowledged opening lore, five weighted encounters, immediate
  round-based combat, XP/gold, multi-level progression, absolute cooldown, latest
  10 hunts, lifetime statistics, and four saveable tabs. Inventory/equipment and
  future locations remain placeholders; no items, networking, or unlock mechanics.
- Architecture: pure JVM `domain` rules/contracts; Android `app` UI, presentation,
  data/persistence, and manual DI. HuntRepository performs atomic operations;
  Room v3 preserves saves through explicit v1/v2 migrations. UI consumes saved
  results, with a short reveal independent of combat calculation.
- Configuration: `domain/src/main/kotlin/com/example/lootrpg/hunt/domain/GameConfig.kt`
  owns cooldown values: debug 10 seconds, release/default 15 minutes. AppContainer
  selects via BuildConfig.DEBUG. Static content: GameDefinitions.kt; XP/stat rules:
  player/domain/Progression.kt. Device time is sampled through TimeProvider.
- Key paths: `domain/src/main/kotlin`, `app/src/main/kotlin`, `app/schemas`,
  `gradle/libs.versions.toml`; JVM tests in each module's `src/test`, integration
  tests in `app/src/androidTest`. Build/setup commands: README.
- Verified: debug and unsigned release APK builds, 31 JVM tests, 9 device tests,
  lint with no issues. API 37 emulator: opening, all encounters, leveling, tabs,
  cooldown/reward/history restart persistence, 320dp width with 130% font size.
  Ten-minute ADB-assisted live playtest: 46 hunts, Level 4. See PLAYTEST_M2.md.
- Approved limitation: supplied stats make natural defeat impossible; defeat is
  covered with deterministic weak-player fixtures, including UI and persistence.
  Level 5 banks XP because later thresholds are TBD. No silent rebalancing.
- Remaining limitations: one local profile, mutable device time, placeholder
  visuals, no release signing; API 26/physical devices not exercised. Upstream
  JDK 25 protobuf Unsafe and native-symbol stripping warnings are nonblocking.
- Next: Milestone 3 scope to be agreed. Do not implement it without a request.

Read ARCHITECTURE for boundaries, GAME_DESIGN for rules/TBDs, DATA_MODEL for
storage, DECISIONS for rationale, and ROADMAP for milestone direction.
