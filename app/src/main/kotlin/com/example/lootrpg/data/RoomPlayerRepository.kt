package com.example.lootrpg.data

import com.example.lootrpg.data.persistence.PlayerDao
import com.example.lootrpg.player.domain.Player
import com.example.lootrpg.player.domain.PlayerRepository

internal class RoomPlayerRepository(private val dao: PlayerDao) : PlayerRepository {
    override suspend fun getOrCreate(initialPlayer: Player): Player =
        dao.getOrCreate(initialPlayer.toEntity()).toDomain()
}
