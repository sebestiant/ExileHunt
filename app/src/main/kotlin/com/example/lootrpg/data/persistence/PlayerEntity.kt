package com.example.lootrpg.data.persistence

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player")
internal data class PlayerEntity(
    @PrimaryKey val slot: Int = 1,
    val playerId: String,
    val name: String,
    val level: Int,
    val currentXp: Long,
    val gold: Long,
    val attack: Int,
    val defense: Int,
    val maxHealth: Int,
)
