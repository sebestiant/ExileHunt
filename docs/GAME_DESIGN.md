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

Combat formulas, stats, classes, rarity tiers, drop probabilities, item generation,
monsters, areas, XP curves, unlock rules, failure rewards, exact cooldown policy,
offline clock-change handling, monetization, and backend design: **TBD**.

Milestone 0 implements none of these mechanics. Do not infer game rules from the
temporary foundation persistence component.
