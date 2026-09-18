package com.example.lootrpg.player.domain

class LoadPlayerUseCase(private val repository: PlayerRepository) {
    suspend operator fun invoke(): Player = repository.getOrCreate(Player.newAdventurer())
}
