package com.example.lootrpg.player.domain

import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Test

class LoadPlayerUseCaseTest {
    @Test
    fun `first load creates the specified default player`() = runTest {
        val repository = MemoryPlayerRepository()
        val player = LoadPlayerUseCase(repository)()
        assertEquals(PlayerId("local-player"), player.id)
        assertEquals("Adventurer", player.name)
        assertEquals(1, player.level)
        assertEquals(0L, player.currentXp)
        assertEquals(100L, player.gold)
        assertEquals(CombatStats(10, 5, 100), player.stats)
        assertSame(player, repository.saved)
    }

    @Test
    fun `existing player is returned unchanged on repeated loads`() = runTest {
        val existing = Player.newAdventurer().copy(name = "Returning adventurer", gold = 217)
        val repository = MemoryPlayerRepository(existing)
        val load = LoadPlayerUseCase(repository)
        assertSame(existing, load())
        assertSame(existing, load())
    }

    @Test
    fun `storage failure propagates to presentation`() = runTest {
        val failure = IllegalStateException("Storage unavailable")
        try {
            LoadPlayerUseCase(PlayerRepository { throw failure })()
            throw AssertionError("Expected failure")
        } catch (actual: IllegalStateException) {
            assertSame(failure, actual)
        }
    }

    @Test
    fun `invalid player values are rejected`() {
        val player = Player.newAdventurer()
        assertThrows(IllegalArgumentException::class.java) { player.copy(name = " ") }
        assertThrows(IllegalArgumentException::class.java) { player.copy(level = 0) }
        assertThrows(IllegalArgumentException::class.java) { player.copy(currentXp = -1) }
        assertThrows(IllegalArgumentException::class.java) { player.copy(gold = -1) }
        assertThrows(IllegalArgumentException::class.java) { CombatStats(-1, 5, 100) }
        assertThrows(IllegalArgumentException::class.java) { CombatStats(10, -1, 100) }
        assertThrows(IllegalArgumentException::class.java) { CombatStats(10, 5, 0) }
        assertThrows(IllegalArgumentException::class.java) { PlayerId("") }
    }

    private class MemoryPlayerRepository(var saved: Player? = null) : PlayerRepository {
        override suspend fun getOrCreate(initialPlayer: Player): Player =
            saved ?: initialPlayer.also { saved = it }
    }
}
