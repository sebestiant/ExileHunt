package com.example.lootrpg.data.persistence

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recent_hunt")
internal data class HuntEntity(
    @PrimaryKey val sequence: Long,
    val monsterId: String, val monsterName: String, val monsterLevel: Int,
    val monsterDescription: String, val outcome: String, val rounds: Int,
    val damageDealt: Long, val damageTaken: Long, val roundLimitReached: Boolean,
    val experienceGained: Long, val goldGained: Long,
    val occurredAtEpochMillis: Long, val nextHuntAtEpochMillis: Long,
    @Embedded(prefix = "levelUp_") val levelUp: LevelUpEntity?,
)

internal data class LevelUpEntity(
    val fromLevel: Int, val toLevel: Int,
    val beforeAttack: Int, val beforeDefense: Int, val beforeHealth: Int,
    val afterAttack: Int, val afterDefense: Int, val afterHealth: Int,
)
