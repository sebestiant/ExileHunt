package com.example.lootrpg.hunt.domain

import com.example.lootrpg.player.domain.CombatStats

data class MonsterDefinition(
    val id: String, val name: String, val level: Int, val description: String,
    val stats: CombatStats, val experience: Long, val gold: IntRange,
)

data class EncounterEntry(val monster: MonsterDefinition, val weight: Int) {
    init { require(weight > 0) }
}

data class AreaDefinition(
    val id: String, val name: String, val recommendedLevels: IntRange,
    val description: String, val encounters: List<EncounterEntry>,
)

/** Static content, never mutable player/save state. */
object GameDefinitions {
    val wildOutskirts = AreaDefinition(
        id = "wild-outskirts", name = "The Wild Outskirts", recommendedLevels = 1..3,
        description = "A broken frontier of abandoned roads, dead farmland and wilderness slowly reclaiming what civilization left behind.",
        encounters = listOf(
            EncounterEntry(MonsterDefinition("rotted-hound", "Rotted Hound", 1,
                "A starving beast twisted by whatever now poisons the wilderness.", CombatStats(5, 1, 35), 12, 4..8), 30),
            EncounterEntry(MonsterDefinition("thorn-rat", "Thorn Rat", 1,
                "An oversized scavenger covered in hardened, thorn-like growths.", CombatStats(4, 2, 28), 10, 3..7), 25),
            EncounterEntry(MonsterDefinition("roadside-marauder", "Roadside Marauder", 2,
                "A desperate outlaw who discovered that travellers are easier prey than monsters.", CombatStats(7, 3, 48), 18, 8..14), 20),
            EncounterEntry(MonsterDefinition("hollow-wanderer", "Hollow Wanderer", 2,
                "A silent figure that walks the abandoned roads long after death should have stopped it.", CombatStats(8, 2, 55), 22, 7..12), 15),
            EncounterEntry(MonsterDefinition("fractured-boar", "Fractured Boar", 3,
                "A massive boar warped by strange growths beneath its hide.", CombatStats(11, 4, 75), 32, 12..20), 10),
        ),
    )
}
