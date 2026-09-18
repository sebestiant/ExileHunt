# Milestone 2 playtest and verification

Date: 2026-09-18. Pixel_10_Pro emulator, API 37, debug configuration.

## Ten-minute session

An ADB-assisted real-time playtest ran for 600.07 seconds starting at
14:52:48 UTC. A script tapped the actual available HUNT button and read the
rendered results. It did not advance the clock, force encounters, bypass the
cooldown, or change probabilities. Screenshots and navigation were inspected
separately. This was not a wholly manual human play session.

| Measure | Observed value |
| --- | --- |
| Hunts | 46 |
| Victories / defeats | 46 / 0 |
| Rotted Hound | 15 |
| Thorn Rat | 12 |
| Roadside Marauder | 9 |
| Hollow Wanderer | 7 |
| Fractured Boar | 3 |
| Final level | 4 |
| Final current XP | 237 / 325 |
| Final gold | 468 (started at 100) |
| Final health / attack / defense | 130 / 16 / 8 |

UI-tree capture/polling added overhead, so this run performed fewer than the
roughly 60 hunts theoretically possible with a 10-second cooldown. Character
statistics independently confirmed the 46 hunts and final progression. Two
additional hunts used for restart/layout checks are excluded from these totals.

## Observations

- Hunt remained prominent and accessible while results/history scrolled.
- The brief reveal and explicit victory/reward card made outcomes readable.
  Level 2 appeared on hunt 8; subsequent stat increases were visible on Character.
- The 10-second rhythm supported repeated play, with no stuck cooldown or
  duplicate reward observed. The supplied rewards reached Level 4 within ten minutes.
- All five encounters appeared naturally. At higher levels, early monsters became
  very easy; there is no level-based encounter scaling in the specified rules.
- As approved by the user, natural defeat is impossible with the supplied stats.
  No live numbers were changed to force defeat. Weak-player fixtures verify defeat
  rules, zero rewards, saved counters, and result rendering.
- Repeated encounters use the same short feedback sequence; subjective long-term
  enjoyment still needs human playtesting. No broad redesign was inferred.
- Review prompted two small presentation refinements: readable disabled countdown
  text and scrolling to the latest result when starting another hunt. These were
  checked on the final APK after the timed session; game rules were unchanged.
- At 320dp width with 130% font scaling, Hunt/navigation remain accessible and the
  result requires scrolling. This favors action access over showing every statistic
  simultaneously. Emulator size, density, and font settings were restored.

## Additional emulator checks

Opening acknowledgment persisted. All four destinations worked; Inventory remained
empty and World showed the current area plus locked placeholders. Force-stop/relaunch
preserved the level, XP, gold, latest result, and recent history. Restart immediately
after an extra hunt showed the remaining cooldown (00:05), without replaying lore or
resetting rewards. The final APK was also installed and launched successfully.

## Automated verification

All commands use the repository Gradle wrapper:

```powershell
./gradlew.bat :app:assembleDebug :app:assembleRelease :domain:test :app:testDebugUnitTest :app:lintDebug
./gradlew.bat :app:connectedDebugAndroidTest
```

Both succeeded. 31 JVM tests and 9 emulator tests passed; lint reported no issues.
Coverage includes clock eligibility boundaries, weighted selection, reproducible
combat and round cap, minimum damage, rewards, multi-level carry/stat growth,
ViewModel states/errors/ticking, migration from v1/v2, reopening saves, concurrent
hunts, history pruning, and rollback after an injected SQLite write failure.
A rendering fixture verifies defeat and level-up text without altering live balance.

Release builds select the 15-minute production configuration; signing/distribution
is not configured. API 26 and physical devices were not tested. Existing upstream
protobuf Unsafe/native-symbol warnings did not prevent verification.
