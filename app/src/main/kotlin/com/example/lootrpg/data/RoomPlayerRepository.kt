package com.example.lootrpg.data

import com.example.lootrpg.data.persistence.PlayerDao
import com.example.lootrpg.data.persistence.PlayerEntity
import com.example.lootrpg.player.domain.CombatStats
import com.example.lootrpg.player.domain.Player
import com.example.lootrpg.player.domain.PlayerId
import com.example.lootrpg.player.domain.PlayerRepository

internal class RoomPlayerRepository(private val dao: PlayerDao) : PlayerRepository {
    override suspend fun getOrCreate(initialPlayer: Player): Player {
        val stored = dao.getOrCreate(PlayerEntity(
            playerId = initialPlayer.id.value,
            name = initialPlayer.name,
            level = initialPlayer.level,
            currentXp = initialPlayer.currentXp,
            gold = initialPlayer.gold,
            attack = initialPlayer.stats.attack,
            defense = initialPlayer.stats.defense,
            maxHealth = initialPlayer.stats.maxHealth,
        ))
        return Player(
            id = PlayerId(stored.playerId),
            name = stored.name,
            level = stored.level,
            currentXp = stored.currentXp,
            gold = stored.gold,
            stats = CombatStats(stored.attack, stored.defense, stored.maxHealth),
        )
    }
}
