# Data model

## PLAYER STATE - implemented

Domain Player has PlayerId (nonblank string), name, level, currentXp, gold, and
CombatStats (attack, defense, maxHealth). Names must be nonblank; level/health
positive; XP, gold, attack, and defense nonnegative. XP and gold use Long.

Defaults live in Player.newAdventurer(): local-player, Adventurer, level 1, XP 0,
gold 100, attack 10, defense 5, maxHealth 100. The ID identifies one offline
profile, not a future account. No update/delete/reward/equipment APIs exist.

Room database lootrpg.db, version 2; schemas exported under app/schemas/.

| Player table column | Type | Meaning |
| --- | --- | --- |
| slot | INTEGER primary key | Internal singleton slot 1, separate from domain identity |
| playerId, name | TEXT non-null | Domain identity and display name |
| level | INTEGER non-null | Player level |
| currentXp, gold | INTEGER non-null | Long-valued counters |
| attack, defense, maxHealth | INTEGER non-null | Basic combat stats |

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

## STATIC GAME DEFINITIONS - not persisted

The Level 1 display target is 100 XP, centralized in ExperienceDisplay. It is not
a progression formula. Area names/descriptions and locked labels for the upcoming
shell are presentation placeholders, not area domain models or player unlock state.
Monsters, item bases, affixes, rarities, loot tables, XP curves, and progression
rules remain conceptual; schemas, identifiers, and storage formats are TBD.

## Future player state - conceptual only

Inventory, equipment, unlocked areas, hunt history, and cooldowns have no models or
tables. Future cooldowns will persist absolute timestamps such as nextHuntAt.
