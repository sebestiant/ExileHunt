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

Combat formulas, additional stats, classes, rarity tiers, drop probabilities, item generation,
monsters, areas, XP curves, unlock rules, failure rewards, exact cooldown policy,
offline clock-change handling, monetization, and backend design: **TBD**.

## Milestone 1 foundation

The default player is Adventurer, Level 1, XP 0, Gold 100, Attack 10, Defense 5,
and Max Health 100. The Level 1 XP display target is 100; no XP gain or progression
formula is implemented. Player values persist locally and are not reset on launch.
