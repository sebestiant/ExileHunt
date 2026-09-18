package com.example.lootrpg.hunt.domain

import com.example.lootrpg.core.domain.RandomProvider

class SelectEncounterUseCase(private val random: RandomProvider) {
    operator fun invoke(table: List<EncounterEntry>): MonsterDefinition {
        require(table.isNotEmpty())
        val total = table.fold(0) { sum, entry -> Math.addExact(sum, entry.weight) }
        val roll = random.nextInt(total)
        var boundary = 0
        return table.first { boundary += it.weight; roll < boundary }.monster
    }
}
