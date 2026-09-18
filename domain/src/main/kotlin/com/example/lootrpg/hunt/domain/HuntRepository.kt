package com.example.lootrpg.hunt.domain

import com.example.lootrpg.player.domain.Player

interface HuntRepository {
    suspend fun load(initialPlayer: Player): HuntState
    suspend fun acknowledgeIntroduction(): HuntState
    /** Resolve once against current saved state; commit player, history, and cooldown atomically. */
    suspend fun performHunt(): HuntAttempt
}

class LoadHuntUseCase(private val repository: HuntRepository) {
    suspend operator fun invoke(): HuntState = repository.load(Player.newAdventurer())
}

class PerformHuntUseCase(private val repository: HuntRepository) {
    suspend operator fun invoke(): HuntAttempt = repository.performHunt()
}

class AcknowledgeIntroductionUseCase(private val repository: HuntRepository) {
    suspend operator fun invoke(): HuntState = repository.acknowledgeIntroduction()
}
