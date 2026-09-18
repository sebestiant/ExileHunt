package com.example.lootrpg.player.domain

import java.time.Instant

@JvmInline
value class PlayerId(val value: String) {
    init { require(value.isNotBlank()) }
}

data class CombatStats(val attack: Int, val defense: Int, val maxHealth: Int) {
    init {
        require(attack >= 0 && defense >= 0)
        require(maxHealth > 0)
    }
}

data class Player(
    val id: PlayerId,
    val name: String,
    val level: Int,
    val currentXp: Long,
    val gold: Long,
    val stats: CombatStats,
    val nextHuntAt: Instant? = null,
    val introductionAcknowledged: Boolean = false,
    val victories: Long = 0,
    val defeats: Long = 0,
) {
    val totalHunts: Long get() = Math.addExact(victories, defeats)
    init {
        require(name.isNotBlank())
        require(level >= 1)
        require(currentXp >= 0 && gold >= 0)
        require(victories >= 0 && defeats >= 0)
    }

    companion object {
        /** One offline profile; this identifier is not an account or authentication identity. */
        fun newAdventurer() = Player(
            id = PlayerId("local-player"),
            name = "Adventurer",
            level = 1,
            currentXp = 0,
            gold = 100,
            stats = CombatStats(attack = 10, defense = 5, maxHealth = 100),
        )
    }
}
