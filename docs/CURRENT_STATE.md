# Current state

- Milestone: **0 — Project Foundation complete** (2026-09-18).
- App: LootRPG (temporary), `com.example.lootrpg`; minimum Android API 26.
- Implemented: Material 3 Compose placeholder; ViewModel/StateFlow loading, ready,
  and retry states; manual DI; injectable time and RNG; Room integration marker.
- Architecture: JVM-only `domain` module; `app` packages separate UI, presentation,
  data/persistence, and dependency wiring. No gameplay or networking.
- Key paths: `domain/src/main/kotlin`, `app/src/main/kotlin`, `app/schemas`,
  `gradle/libs.versions.toml`; unit tests in each module's `src/test`, Room test in
  `app/src/androidTest`.
- Verified: `:app:assembleDebug`; `:domain:test` (4 tests) and
  `:app:testDebugUnitTest` (6 tests); `:app:lintDebug` (no issues);
  `:app:connectedDebugAndroidTest` (1 Room persistence test); `:app:installDebug`.
  Cold launch on Pixel_10_Pro API 37 returned `Status: ok`; UI inspection confirmed
  "The prototype is ready." Commands/prerequisites: README. API 26 not device-tested.
- Tooling warnings: JDK 25 exposes an upstream protobuf Unsafe deprecation during
  device testing; an AndroidX native library is packaged without symbol stripping.
  Neither prevented validation. No dependency downgrade or extra NDK was needed.
- Limitations/debt: temporary marker must be migrated away when real persistence
  replaces it; device time is mutable; no release signing, game schema, or backend.
- Next expected milestone: agree application-shell scope (Milestone 1). Not started.

Read ARCHITECTURE for boundaries, GAME_DESIGN for known rules/TBDs, DATA_MODEL for
persisted concepts, DECISIONS for rationale, and ROADMAP for milestone direction.
