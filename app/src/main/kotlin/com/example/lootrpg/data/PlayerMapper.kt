package com.example.lootrpg.data

import com.example.lootrpg.data.persistence.PlayerEntity
import com.example.lootrpg.player.domain.CombatStats
import com.example.lootrpg.player.domain.Player
import com.example.lootrpg.player.domain.PlayerId
import java.time.Instant

internal fun Player.toEntity() = PlayerEntity(
    playerId = id.value, name = name, level = level, currentXp = currentXp, gold = gold,
    attack = stats.attack, defense = stats.defense, maxHealth = stats.maxHealth,
    nextHuntAtEpochMillis = nextHuntAt?.toEpochMilli(),
    introductionAcknowledged = introductionAcknowledged, victories = victories, defeats = defeats,
)

internal fun PlayerEntity.toDomain() = Player(
    id = PlayerId(playerId), name = name, level = level, currentXp = currentXp, gold = gold,
    stats = CombatStats(attack, defense, maxHealth),
    nextHuntAt = nextHuntAtEpochMillis?.let(Instant::ofEpochMilli),
    introductionAcknowledged = introductionAcknowledged, victories = victories, defeats = defeats,
)
