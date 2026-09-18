package com.example.lootrpg.hunt.domain

import com.example.lootrpg.player.domain.CombatStats
import com.example.lootrpg.player.domain.Player
import java.time.Instant

data class LevelUp(val fromLevel: Int, val toLevel: Int, val before: CombatStats, val after: CombatStats)

/** Immutable resolved encounter, including display snapshots that survive content changes. */
data class HuntResult(
    val sequence: Long, val monsterId: String, val monsterName: String,
    val monsterLevel: Int, val monsterDescription: String,
    val combat: CombatResult, val experienceGained: Long, val goldGained: Long,
    val occurredAt: Instant, val nextHuntAt: Instant, val levelUp: LevelUp?,
)

data class HuntState(val player: Player, val recentHunts: List<HuntResult> = emptyList())

sealed interface HuntAttempt {
    data class Completed(val state: HuntState, val result: HuntResult) : HuntAttempt
    data class CoolingDown(val state: HuntState) : HuntAttempt
    data class IntroductionRequired(val state: HuntState) : HuntAttempt
}

fun canHunt(nextHuntAt: Instant?, now: Instant): Boolean = nextHuntAt == null || !now.isBefore(nextHuntAt)
