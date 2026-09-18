package com.example.lootrpg.player.domain

fun interface PlayerRepository {
    /** Atomically load the existing profile, or persist [initialPlayer] if none exists.
     * Concurrent calls must return the same persisted profile and never reset existing state.
     */
    suspend fun getOrCreate(initialPlayer: Player): Player
}
