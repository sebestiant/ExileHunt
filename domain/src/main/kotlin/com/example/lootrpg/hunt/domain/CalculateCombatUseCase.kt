package com.example.lootrpg.hunt.domain

import com.example.lootrpg.core.domain.RandomProvider
import com.example.lootrpg.player.domain.CombatStats
import kotlin.math.roundToInt

enum class CombatOutcome { Victory, Defeat }

data class CombatResult(
    val outcome: CombatOutcome, val rounds: Int, val damageDealt: Long,
    val damageTaken: Long, val roundLimitReached: Boolean = false,
)

class CalculateCombatUseCase(private val random: RandomProvider) {
    operator fun invoke(player: CombatStats, monster: CombatStats): CombatResult {
        var playerHealth = player.maxHealth.toLong()
        var monsterHealth = monster.maxHealth.toLong()
        var dealt = 0L
        var taken = 0L
        for (round in 1..MAX_ROUNDS) {
            val hit = damage(player.attack, monster.defense).coerceAtMost(monsterHealth)
            monsterHealth -= hit
            dealt += hit
            if (monsterHealth == 0L) return CombatResult(CombatOutcome.Victory, round, dealt, taken)
            val counter = damage(monster.attack, player.defense).coerceAtMost(playerHealth)
            playerHealth -= counter
            taken += counter
            if (playerHealth == 0L) return CombatResult(CombatOutcome.Defeat, round, dealt, taken)
        }
        // Defensive stalemate policy: retreat, no reward, normal cooldown.
        return CombatResult(CombatOutcome.Defeat, MAX_ROUNDS, dealt, taken, roundLimitReached = true)
    }

    private fun damage(attack: Int, defense: Int): Long =
        ((attack.toLong() - defense).coerceAtLeast(1) * (0.9 + random.nextDouble() * 0.2))
            .roundToInt().coerceAtLeast(1).toLong()

    companion object { const val MAX_ROUNDS = 200 }
}
