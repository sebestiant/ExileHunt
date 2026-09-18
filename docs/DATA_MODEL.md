# Data model

## PLAYER STATE - implemented

Domain Player has PlayerId (nonblank string), name, level, currentXp, gold, and
CombatStats (attack, defense, maxHealth). Names must be nonblank; level/health
positive; XP, gold, attack, and defense nonnegative. XP and gold use Long.

Defaults live in Player.newAdventurer(): local-player, Adventurer, level 1, XP 0,
gold 100, attack 10, defense 5, maxHealth 100. The ID identifies one offline
profile, not a future account. Gameplay mutations are exposed only through HuntRepository.

Room database lootrpg.db, version 3; schemas exported under app/schemas/.

| Player table column | Type | Meaning |
| --- | --- | --- |
| slot | INTEGER primary key | Internal singleton slot 1, separate from domain identity |
| playerId, name | TEXT non-null | Domain identity and display name |
| level | INTEGER non-null | Player level |
| currentXp, gold | INTEGER non-null | Long-valued counters |
| attack, defense, maxHealth | INTEGER non-null | Basic combat stats |
| nextHuntAtEpochMillis | INTEGER nullable | Absolute UTC eligibility time; null before first hunt |
| introductionAcknowledged | INTEGER non-null, default 0 | One-time opening acknowledgment |
| victories, defeats | INTEGER non-null, default 0 | Lifetime counts; total is their sum |

PlayerRepository.getOrCreate has atomic load-or-create semantics. Room reads,
inserts if absent, and returns the persisted row in a transaction. Existing values
are never replaced by defaults. Database entities stay inside Data.

## Migration history

Version 1 had only foundation_marker, a temporary internal integration probe.
Migration 1 -> 2 explicitly creates player and drops that marker. Default creation
still goes through the domain use case after migration; SQL contains no duplicated
player defaults. Exported v1 schema is retained and exercised by a device test.
There is no destructive fallback. Android cloud backup/device transfer remain
disabled; export/cloud-save policy is TBD.

Migration 2 -> 3 preserves the player, adds the four hunt-state columns, and creates
recent_hunt. Both exported v1 and v2 migrations are device-tested. Recent hunts use
the lifetime hunt sequence as primary key, ordered newest first and pruned to 10.
Each row snapshots monster ID/name/level/description, outcome, rounds, actual damage,
round-cap flag, XP/gold gained, encounter/next-hunt timestamps, and optional before/
after level/stat values. These snapshots preserve the last result across restart.

RoomHuntRepository serializes reading current state, domain eligibility/resolution,
player update, history insertion, and pruning in one Room transaction. A write error
rolls back rewards, stats, counts, history, and cooldown. Concurrent requests observe
the updated cooldown rather than granting duplicate rewards. Intro acknowledgment
updates the latest player transactionally. Domain contains no Room types.

## STATIC GAME DEFINITIONS - not persisted

GameDefinitions contains the actual Wild Outskirts area and its five weighted monster
definitions; Progression contains XP thresholds/stat growth. These are Kotlin game
configuration, never mixed with save tables. Locked future areas remain presentation
placeholders. Item bases, affixes, rarities, and loot tables are not implemented.

## Future player state - conceptual only

Inventory, equipment, and unlocked areas have no models or tables. There are no
persistent injuries, items, quests, or additional RPG stats.
