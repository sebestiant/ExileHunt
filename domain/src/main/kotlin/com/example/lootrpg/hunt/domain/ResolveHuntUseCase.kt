package com.example.lootrpg.hunt.domain

import com.example.lootrpg.core.domain.RandomProvider
import com.example.lootrpg.core.domain.TimeProvider
import com.example.lootrpg.player.domain.Progression

/** Pure rules invoked against the latest snapshot within the local repository's atomic operation. */
class ResolveHuntUseCase(
    private val clock: TimeProvider,
    private val random: RandomProvider,
    private val config: GameConfig,
    private val area: AreaDefinition = GameDefinitions.wildOutskirts,
) {
    operator fun invoke(state: HuntState): HuntAttempt {
        val now = clock.now()
        val player = state.player
        if (!player.introductionAcknowledged) return HuntAttempt.IntroductionRequired(state)
        if (!canHunt(player.nextHuntAt, now)) return HuntAttempt.CoolingDown(state)
        val monster = SelectEncounterUseCase(random)(area.encounters)
        val combat = CalculateCombatUseCase(random)(player.stats, monster.stats)
        val victory = combat.outcome == CombatOutcome.Victory
        val xp = if (victory) monster.experience else 0
        val gold = if (victory) (monster.gold.first + random.nextInt(monster.gold.last - monster.gold.first + 1)).toLong() else 0
        val updated = Progression.award(player, xp, gold).copy(
            nextHuntAt = now.plus(config.huntCooldown),
            victories = Math.addExact(player.victories, if (victory) 1 else 0),
            defeats = Math.addExact(player.defeats, if (victory) 0 else 1),
        )
        val result = HuntResult(
            updated.totalHunts, monster.id, monster.name, monster.level, monster.description,
            combat, xp, gold, now, checkNotNull(updated.nextHuntAt),
            if (updated.level > player.level) LevelUp(player.level, updated.level, player.stats, updated.stats) else null,
        )
        return HuntAttempt.Completed(HuntState(updated, (listOf(result) + state.recentHunts).take(10)), result)
    }
}
