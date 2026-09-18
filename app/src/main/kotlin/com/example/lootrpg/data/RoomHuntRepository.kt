package com.example.lootrpg.data

import androidx.room.withTransaction
import com.example.lootrpg.data.persistence.LootRpgDatabase
import com.example.lootrpg.hunt.domain.HuntAttempt
import com.example.lootrpg.hunt.domain.HuntRepository
import com.example.lootrpg.hunt.domain.HuntState
import com.example.lootrpg.hunt.domain.ResolveHuntUseCase
import com.example.lootrpg.player.domain.Player

internal class RoomHuntRepository(
    private val database: LootRpgDatabase,
    private val resolveHunt: ResolveHuntUseCase,
) : HuntRepository {
    override suspend fun load(initialPlayer: Player): HuntState = database.withTransaction {
        database.playerDao().getOrCreate(initialPlayer.toEntity())
        readState()
    }

    override suspend fun acknowledgeIntroduction(): HuntState = database.withTransaction {
        val current = readState()
        database.playerDao().update(current.player.copy(introductionAcknowledged = true).toEntity())
        readState()
    }

    override suspend fun performHunt(): HuntAttempt = database.withTransaction {
        // Eligibility and RNG run only after obtaining the latest serialized state.
        val attempt = resolveHunt(readState())
        if (attempt is HuntAttempt.Completed) {
            database.playerDao().update(attempt.state.player.toEntity())
            database.huntDao().insert(attempt.result.toEntity())
            database.huntDao().prune()
        }
        attempt
    }

    private suspend fun readState() = HuntState(
        checkNotNull(database.playerDao().read()) { "Load player before hunting" }.toDomain(),
        database.huntDao().recent().map { it.toDomain() },
    )
}
