# Game design

## Established concept

- Lightweight 2D asynchronous loot RPG; no game engine or real-time combat.
- The player will initiate periodic hunts, intended approximately every 15 minutes.
- Encounters will resolve combat automatically.
- Monsters will support progression; rewards will include XP, gold, and loot.
- Equipment will improve character strength; areas will become progressively harder.
- Loot and progression are central to the experience.
- Initial implementation is offline; a server-backed game is a future direction.

MouseHunt informs the interaction style; loot-driven ARPGs inform the progression
philosophy. All actual content and presentation must be original.

## Undecided (TBD)

Additional stats, classes, rarity tiers, drop probabilities, item generation,
future monsters/areas, Level 5+ XP requirements, unlock rules,
offline clock-change handling, monetization, and backend design: **TBD**.

## Player foundation

The default player is Adventurer, Level 1, XP 0, Gold 100, Attack 10, Defense 5,
and Max Health 100. Player values persist locally and are not reset on launch.

The shell has Hunt, Character, Inventory, and World destinations. The current area
is The Wild Outskirts. Ashen Woods, Forgotten Ruins, and Blackstone Pass are temporary
locked location labels, not unlock mechanics or area definitions.

Character displays Weapon, Helmet, Armour, Gloves, and Boots as Empty; inventory
is an empty-state placeholder. There are no item or equipment models.

Visual direction: original dark fantasy presentation, charcoal panels, muted gold
accents, readable system typography, simple silhouettes, and prominent primary action.

## Milestone 2 rules

Static definitions live in domain/hunt/domain/GameDefinitions.kt. The Wild Outskirts
is the only playable area (recommended levels 1-3): a broken frontier of abandoned
roads, dead farmland and wilderness slowly reclaiming what civilization left behind.

| Encounter | Level | Health | Attack | Defense | XP | Gold (inclusive) | Weight |
| --- | --- | --- | --- | --- | --- | --- | --- |
| Rotted Hound | 1 | 35 | 5 | 1 | 12 | 4-8 | 30 |
| Thorn Rat | 1 | 28 | 4 | 2 | 10 | 3-7 | 25 |
| Roadside Marauder | 2 | 48 | 7 | 3 | 18 | 8-14 | 20 |
| Hollow Wanderer | 2 | 55 | 8 | 2 | 22 | 7-12 | 15 |
| Fractured Boar | 3 | 75 | 11 | 4 | 32 | 12-20 | 10 |

Select a uniform integer in [0, total weight); cumulative weights choose the monster.
Combat starts with full health each hunt, player attacks first. Each hit is
max(1, round(max(1, attack - defense) * (0.9 + RNG * 0.2))). RNG is in [0, 1).
Round to nearest integer; ties round up. Damage totals count actual HP removed,
excluding overkill. Stop immediately at zero HP; no final retaliation after a kill.
At the defensive 200-round cap, retreat counts as defeat. No persistent injuries.
Victory grants monster XP and uniformly sampled inclusive gold; defeat grants
neither and never subtracts gold. Both outcomes consume the normal cooldown.

GameConfig.kt is the single cooldown configuration: Development = 10 seconds,
Production/default = 15 minutes. Debug wiring selects Development; release selects
Production. Eligibility is checked by the resolver using TimeProvider inside the
atomic hunt. nextHuntAt is an absolute timestamp; a UI countdown has no authority.

XP thresholds for levels 1->2, 2->3, 3->4, 4->5: 100, 150, 225, 325.
Subtract thresholds and carry surplus XP, allowing multiple level-ups. Each level
adds Health +10, Attack +2, Defense +1. At Level 5 retain XP, but do not invent
further requirements or grant more levels. No stat allocation.

### Approved balancing limitation

The user explicitly chose to preserve every specified monster stat. These numbers
make natural defeat impossible for the starting player, even with worst-case RNG:
the boar's maximum 14 counterattacks deal at most 98 damage. Leveling only makes
this easier. Defeat is implemented/tested with weakened-player fixtures, not forced
into live encounters. The supplied XP curve also allows reaching Level 4-5 in a
ten-minute session; there is no hidden slowing of rewards or scripted encounter bias.

### Story

The Wardstones once sheltered settlements. The unexplained Fracture shattered
them; roads vanished, settlements became isolated, creatures emerged, and ruins
appeared where none had stood. Some believe the land itself changed. Adventurer
is an unknown wanderer arriving in Greyhaven, surviving on hunting contracts, not
a chosen hero. The first Hunt entry introduces this briefly and persists acknowledgment.
No quests, NPC systems, or equipment are implied by the narrative.
