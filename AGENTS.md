# Repository instructions

ExileHunt is an Android 2D asynchronous loot RPG. It is
offline today and must support a future server-backed implementation. Use original
content; never copy proprietary assets, text, characters, names, or UI designs.

## Start here

1. Read this file, then `docs/CURRENT_STATE.md`.
2. Read `docs/ARCHITECTURE.md` only when architectural context is needed.
3. Read `docs/GAME_DESIGN.md` when working on game rules.
4. Read `docs/DATA_MODEL.md` when changing models or persistence.
5. Read `docs/DECISIONS.md` when making or revisiting architecture decisions.
6. Read applicable scoped AGENTS.md files if present.
7. Inspect code related to the requested feature before broadening the search.

DO NOT recursively reread or analyze the entire repository for every task.
Start with these context documents and targeted inspection. Documentation is
never a substitute for inspecting the actual code being modified.

## Engineering rules

- Kotlin, Compose, Material 3, ViewModel, Coroutines, Flow/StateFlow, Room,
  Gradle Kotlin DSL, JUnit. Prefer official Android/Jetpack libraries.
- Keep classes/functions small, APIs explicit, state immutable, and changes scoped.
  Reuse existing patterns; do not add speculative models, abstractions, or modules.
- UI renders state and emits actions. Presentation calls use cases. Domain owns
  business rules and repository interfaces. Data implements those interfaces.
- `domain` is JVM-only: no Android, Compose, Room, database, or network dependencies.
  UI and ViewModels must never access DAOs or concrete repositories.
- Constructor injection; manual wiring in `app/.../di/AppContainer.kt`.
- Room is the current persistence implementation, not the permanent authority.
  Do not introduce networking, accounts, cloud saves, or backend infrastructure
  without an explicit milestone request.
- Inject `TimeProvider`; future cooldowns persist absolute instants (`nextHuntAt`),
  never countdown state. System time belongs only in the local adapter.
- Inject `RandomProvider`; game logic must not instantiate random generators.
- Keep static game definitions separate from mutable player state.
- Do not silently invent/change game rules. State significant assumptions first.
- Add a third-party dependency only for substantial immediate value; explain it first.
- Commit each completed, verified meaningful part with a descriptive commit message
  explaining its purpose. Include affected documentation in that commit. Stage only
  files belonging to the work; preserve unrelated user changes. Do not push unless asked.

## Verification

Use the checked-in Gradle wrapper (PowerShell: `./gradlew.bat`; POSIX: `./gradlew`).
Set JAVA_HOME and ANDROID_HOME as described in README. No IDE is required.

- Build: `./gradlew.bat :app:assembleDebug`
- JVM tests: `./gradlew.bat :domain:test :app:testDebugUnitTest`
- Static checks: `./gradlew.bat :app:lintDebug`
- Room integration on an emulator/device: `./gradlew.bat :app:connectedDebugAndroidTest`

Run relevant tests and build after implementation; fix introduced failures. Test
game rules deterministically on the JVM, including time/RNG edges, rewards,
combat, progression, and eligibility as those mechanics are introduced. Use
instrumentation only when Android behavior requires it. Report commands/results
and any unverified behavior. Review changed files for generated/local artifacts.
Keep Room schema exports under version control; never use destructive migration
as a shortcut once real saves exist. Do not commit SDK paths, caches, or secrets.

## Documentation maintenance

When a task materially changes the repository, update affected documentation in
the same task:

- Architecture: `docs/ARCHITECTURE.md`, and `docs/DECISIONS.md` if significant.
- Game rules: `docs/GAME_DESIGN.md`.
- Persistent models: `docs/DATA_MODEL.md`.
- Milestone completion: `docs/ROADMAP.md`.
- Implementation state: `docs/CURRENT_STATE.md`.

Update only affected documents. Keep CURRENT_STATE concise and current, not a
historical changelog. Do not begin the next milestone without a request.
