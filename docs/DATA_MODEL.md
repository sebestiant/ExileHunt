# Data model

## Implemented: internal integration marker only

Room database `lootrpg.db`, version 1; schema exported under `app/schemas/`.

| Table | Column | Meaning |
| --- | --- | --- |
| foundation_marker | id: INTEGER, primary key | Singleton key 1, internal to Data |
| foundation_marker | initializedAtEpochMillis: INTEGER, non-null | First initialization, UTC epoch milliseconds |

FoundationRepository accepts a domain Instant. The Data implementation maps it to
epoch milliseconds and inserts only if absent. Millisecond precision is intentional.
The timestamp is neither a cooldown nor a player identity. Room needs a concrete
entity for this integration; this marker is a temporary validation component, not
a proposed production save schema. Tests validate persistence across reopening.

No migrations exist before version 1. Preserve exported schema history. Replace or
remove this marker via a deliberate migration when introducing real state. Do not
silently erase saves with destructive fallback. Android cloud backup and device
transfer are excluded via manifest settings and data-extraction rules for this
prototype; export/cloud-save policy is TBD.

## STATIC GAME DEFINITIONS — conceptual, not implemented

Areas, monsters, item bases, affixes, rarity definitions, loot tables, XP curves,
and progression rules. Their storage format, identifiers, versioning, and schema
are TBD. Definitions must not be mixed with mutable player save records.

## PLAYER STATE — conceptual, not implemented

Character, level, XP, gold, inventory, equipment, unlocked areas, hunt history, and
cooldown state. These have no implemented models or database tables. Future
cooldowns persist absolute timestamps such as nextHuntAt, not countdown values.

Repository contracts use domain types; database entities remain in Data. Define
actual models only as needed by an approved milestone and update this document.
