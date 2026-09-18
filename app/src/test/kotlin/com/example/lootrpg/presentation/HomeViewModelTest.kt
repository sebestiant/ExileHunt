package com.example.lootrpg.presentation

import com.example.lootrpg.player.domain.Player
import com.example.lootrpg.player.domain.PlayerRepository
import com.example.lootrpg.player.domain.LoadPlayerUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val dispatcher = StandardTestDispatcher()

    @Before fun setUp() { Dispatchers.setMain(dispatcher) }
    @After fun tearDown() { Dispatchers.resetMain() }

    @Test
    fun `loading becomes ready only after initialization`() = runTest(dispatcher) {
        var calls = 0
        val player = Player.newAdventurer()
        val model = model(PlayerRepository { calls++; player })
        assertEquals(HomeUiState.Loading, model.state.value)
        advanceUntilIdle()
        assertEquals(HomeUiState.Loaded(player, ExperienceDisplay(0, 100)), model.state.value)
        model.retry()
        advanceUntilIdle()
        assertEquals(1, calls)
    }

    @Test
    fun `failure can be retried without duplicate requests`() = runTest(dispatcher) {
        var calls = 0
        val model = model(PlayerRepository {
            if (++calls == 1) throw IllegalStateException("Unavailable")
            Player.newAdventurer()
        })
        advanceUntilIdle()
        assertEquals(HomeUiState.Error, model.state.value)
        model.retry()
        model.retry()
        advanceUntilIdle()
        assertEquals(Player.newAdventurer(), (model.state.value as HomeUiState.Loaded).player)
        assertEquals(2, calls)
    }

    @Test
    fun `cancellation is not converted into an error`() = runTest(dispatcher) {
        val model = model(PlayerRepository { throw CancellationException() })
        advanceUntilIdle()
        assertEquals(HomeUiState.Loading, model.state.value)
    }

    @Test
    fun `existing player values reach the UI unchanged`() = runTest(dispatcher) {
        val existing = Player.newAdventurer().copy(name = "Wayfarer", currentXp = 25, gold = 137)
        val model = model(PlayerRepository { existing })
        advanceUntilIdle()
        assertEquals(HomeUiState.Loaded(existing, ExperienceDisplay(25, 100)), model.state.value)
    }

    private fun model(repository: PlayerRepository) = HomeViewModel(LoadPlayerUseCase(repository))
}
