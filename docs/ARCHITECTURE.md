# Architecture

## Modules and source dependencies

`domain` is Kotlin/JVM with no Android, Room, UI, or network dependencies.
`app` contains Android layers separated by packages. Namespace remains
`com.example.lootrpg` to preserve installed data. No new module or framework was
needed for Milestone 2.

| Source path below namespace | Responsibility |
| --- | --- |
| domain/core/domain | TimeProvider and RandomProvider contracts |
| domain/player/domain | Player, base stats, default profile, XP/stat progression |
| domain/hunt/domain | Static content, weighted selection, combat, cooldown rules, results and operation contracts |
| app/data | Local clock/RNG, repository implementations, entity/domain mapping |
| app/data/persistence | Internal Room v3 database, player/history DAOs, migrations |
| app/presentation | HomeViewModel and immutable HomeUiState |
| app/ui | Screens, saveable tabs, theme, presentation-only graphics |
| app/di | Application-scoped manual composition root |
| */src/test | Deterministic JVM rules and ViewModel tests |
| app/src/androidTest | Real migrations, transactions, restart persistence, result rendering |

## Hunt operation boundary

```text
Compose -> HomeViewModel -> PerformHuntUseCase -> HuntRepository.performHunt
                                                   |
                                  Local RoomHuntRepository
                                  Room transaction:
                                    read current saved state
                                    ResolveHuntUseCase (pure Domain)
                                    save player + result + cooldown; prune history
                                                   |
                                              HuntAttempt
```

Data depends on Domain interfaces and rules, never the reverse. UI/ViewModels do
not access DAOs. Calling pure rules from inside the local repository's transaction
keeps eligibility checks and reward calculation on the same serialized snapshot.
Concurrent hunts cannot both spend the same cooldown; write failure rolls back all
changes. SQLite fault-injection and concurrent-call device tests verify this.

A future remote HuntRepository can return the same HuntAttempt/HuntResult instead
of resolving locally. No remote implementation, generic transaction abstraction,
synchronization, or account infrastructure exists. Room is today's implementation,
not a permanent authority. PlayerRepository's initial load-or-create mapping is
reused by the local hunt repository; only hunt operations can mutate gameplay state.

## State and presentation timing

One Activity-scoped HomeViewModel exposes Loading, Loaded, or Error through
StateFlow. Loaded holds the saved HuntState, remaining seconds, action progress,
presentation phase, and recoverable action errors. Screens render state and emit
actions. Failed loads can retry; failed mutations retain the last good player.
Cancellation is rethrown. A single active operation guards duplicate presses.

Combat is resolved/persisted immediately. Two 650ms presentation pauses reveal
searching and encounter before showing the already-saved result. They never affect
eligibility, damage, or rewards. Closing the app during the reveal cannot lose or
repeat rewards: the latest result is loaded from history on restart.

The Activity runs the ViewModel clock loop only while STARTED. It samples
TimeProvider immediately on resume and every second; remaining time is the ceiling
of nextHuntAt minus now. The domain resolver independently checks eligibility.
No countdown is saved. LocalTimeProvider wraps injectable java.time.Clock;
LocalRandomProvider is the single Kotlin Random adapter. Offline device time can
be manipulated; server authority is a future milestone.

## Configuration and definitions

GameConfig.kt is the sole cooldown configuration (10 seconds Development,
15 minutes Production/default). AppContainer selects with BuildConfig.DEBUG.
Definitions and weights live in GameDefinitions; XP/stat growth in Progression.
ExperienceDisplay formats those rules; it does not own an XP curve. See GAME_DESIGN
for the deliberate Level 5 boundary and approved natural-defeat limitation.

## Navigation and layout

Four peer tabs (Hunt default, Character, Inventory, World) use Material 3
NavigationBar, rememberSaveable selection, and SaveableStateHolder per-tab scroll
state. There is one Activity and no route back stack. Back returns to Hunt.
Reconsider a navigation library only for actual nested routes or deep links.

The Hunt action stays visible while results/history scroll. Starting a hunt returns
that region to the latest result. Character reflects the same player snapshot.
Inventory/equipment/future locations remain placeholders with no repositories.
ExileHuntTheme, shared panels, and ExperienceBar avoid duplicated presentation.
MonsterEmblem tokens are disposable original placeholders. Scaffold owns system
insets. AppContainer constructs dependencies manually; no service locator in rules.
