package com.example.lootrpg.data

import com.example.lootrpg.data.persistence.HuntEntity
import com.example.lootrpg.data.persistence.LevelUpEntity
import com.example.lootrpg.hunt.domain.CombatOutcome
import com.example.lootrpg.hunt.domain.CombatResult
import com.example.lootrpg.hunt.domain.HuntResult
import com.example.lootrpg.hunt.domain.LevelUp
import com.example.lootrpg.player.domain.CombatStats
import java.time.Instant

internal fun HuntResult.toEntity() = HuntEntity(
    sequence, monsterId, monsterName, monsterLevel, monsterDescription, combat.outcome.name,
    combat.rounds, combat.damageDealt, combat.damageTaken, combat.roundLimitReached,
    experienceGained, goldGained, occurredAt.toEpochMilli(), nextHuntAt.toEpochMilli(),
    levelUp?.let { LevelUpEntity(it.fromLevel, it.toLevel,
        it.before.attack, it.before.defense, it.before.maxHealth,
        it.after.attack, it.after.defense, it.after.maxHealth) },
)

internal fun HuntEntity.toDomain() = HuntResult(
    sequence, monsterId, monsterName, monsterLevel, monsterDescription,
    CombatResult(CombatOutcome.valueOf(outcome), rounds, damageDealt, damageTaken, roundLimitReached),
    experienceGained, goldGained, Instant.ofEpochMilli(occurredAtEpochMillis), Instant.ofEpochMilli(nextHuntAtEpochMillis),
    levelUp?.let { LevelUp(it.fromLevel, it.toLevel,
        CombatStats(it.beforeAttack, it.beforeDefense, it.beforeHealth),
        CombatStats(it.afterAttack, it.afterDefense, it.afterHealth)) },
)
