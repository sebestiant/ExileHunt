package com.example.lootrpg.player.domain

object Progression {
    private val requirements = mapOf(1 to 100L, 2 to 150L, 3 to 225L, 4 to 325L)
    fun requiredXp(level: Int): Long? = requirements[level]

    /** XP carries across thresholds. At Level 5, retain XP without inventing a new curve. */
    fun award(player: Player, experience: Long, gold: Long): Player {
        require(experience >= 0 && gold >= 0)
        var xp = Math.addExact(player.currentXp, experience)
        var level = player.level
        var stats = player.stats
        while (true) {
            val required = requiredXp(level) ?: break
            if (xp < required) break
            xp -= required
            level++
            stats = CombatStats(
                Math.addExact(stats.attack, 2), Math.addExact(stats.defense, 1),
                Math.addExact(stats.maxHealth, 10),
            )
        }
        return player.copy(level = level, currentXp = xp, gold = Math.addExact(player.gold, gold), stats = stats)
    }
}
