package com.example.lootrpg.presentation

import com.example.lootrpg.core.domain.RandomProvider
import com.example.lootrpg.core.domain.TimeProvider
import com.example.lootrpg.hunt.domain.*
import com.example.lootrpg.player.domain.Player
import java.time.Instant
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private var now = Instant.EPOCH
    private val clock = TimeProvider { now }
    private val random = object : RandomProvider {
        override fun nextInt(boundExclusive: Int) = 0
        override fun nextDouble() = 0.5
    }
    @Before fun setUp() { Dispatchers.setMain(dispatcher) }
    @After fun tearDown() { Dispatchers.resetMain() }

    @Test fun `loading failure and retry preserve repository player`() = runTest(dispatcher) {
        val repo = MemoryRepository().apply { failLoad = true }
        val model = model(repo)
        assertEquals(HomeUiState.Loading, model.state.value)
        advanceUntilIdle()
        assertEquals(HomeUiState.Error, model.state.value)
        repo.failLoad = false
        model.retry(); model.retry()
        advanceUntilIdle()
        assertEquals(repo.saved.player, loaded(model).player)
    }

    @Test fun `opening acknowledgment must persist before hunting`() = runTest(dispatcher) {
        val repo = MemoryRepository(HuntState(Player.newAdventurer()))
        val model = model(repo)
        advanceUntilIdle()
        assertFalse(loaded(model).huntAvailable)
        model.hunt(); advanceUntilIdle()
        assertEquals(0, repo.huntCalls)
        model.acknowledgeOpening(); advanceUntilIdle()
        assertTrue(loaded(model).huntAvailable)
        assertTrue(repo.saved.player.introductionAcknowledged)
    }

    @Test fun `successful hunt updates player and blocks duplicate presses through reveal`() = runTest(dispatcher) {
        val repo = MemoryRepository()
        val model = model(repo)
        advanceUntilIdle()
        model.hunt(); model.hunt()
        runCurrent()
        assertEquals(1, repo.huntCalls)
        assertEquals(EncounterPhase.Searching, loaded(model).phase)
        assertFalse(loaded(model).huntAvailable)
        advanceTimeBy(650); runCurrent()
        assertEquals(EncounterPhase.Encounter, loaded(model).phase)
        advanceUntilIdle()
        assertEquals(EncounterPhase.Idle, loaded(model).phase)
        assertEquals(12L, loaded(model).player.currentXp)
        assertEquals(104L, loaded(model).player.gold)
        assertEquals(1, loaded(model).snapshot.recentHunts.size)
        assertEquals(10L, loaded(model).remainingSeconds)
    }

    @Test fun `cooldown updates from fake time and ticker without waiting real seconds`() = runTest(dispatcher) {
        val repo = MemoryRepository(HuntState(Player.newAdventurer().copy(
            introductionAcknowledged = true, nextHuntAt = now.plusSeconds(10))))
        val model = model(repo)
        advanceUntilIdle()
        backgroundScope.launch { model.updateClockWhileActive() }
        runCurrent()
        model.hunt(); runCurrent()
        assertEquals(0, repo.huntCalls)
        now = now.plusMillis(9500)
        advanceTimeBy(1000); runCurrent()
        assertEquals(1L, loaded(model).remainingSeconds)
        now = now.plusMillis(500)
        advanceTimeBy(1000); runCurrent()
        assertTrue(loaded(model).huntAvailable)
    }

    @Test fun `storage errors preserve last good state and allow explicit retry`() = runTest(dispatcher) {
        val repo = MemoryRepository().apply { failHunt = true }
        val model = model(repo)
        advanceUntilIdle()
        val before = loaded(model).snapshot
        model.hunt(); advanceUntilIdle()
        assertEquals(before, loaded(model).snapshot)
        assertEquals(before, repo.saved)
        assertEquals(HuntActionError.Hunt, loaded(model).actionError)
        repo.failHunt = false
        model.hunt(); advanceUntilIdle()
        assertEquals(1L, loaded(model).player.totalHunts)
        assertNull(loaded(model).actionError)
    }

    @Test fun `domain cooldown rejection refreshes stale presentation without rewards`() = runTest(dispatcher) {
        val repo = MemoryRepository()
        val model = model(repo)
        advanceUntilIdle()
        repo.saved = repo.saved.copy(player = repo.saved.player.copy(nextHuntAt = now.plusSeconds(5)))
        model.hunt(); advanceUntilIdle()
        assertEquals(5L, loaded(model).remainingSeconds)
        assertEquals(0L, loaded(model).player.totalHunts)
    }

    @Test fun `cancelled loading is not reported as a storage error`() = runTest(dispatcher) {
        val repo = MemoryRepository().apply { cancelLoad = true }
        val model = model(repo)
        advanceUntilIdle()
        assertEquals(HomeUiState.Loading, model.state.value)
    }

    @Test fun `defeat result reaches presentation without awarding rewards`() = runTest(dispatcher) {
        val repo = MemoryRepository(HuntState(Player.newAdventurer().copy(
            stats = com.example.lootrpg.player.domain.CombatStats(1, 0, 1), introductionAcknowledged = true,
        )))
        val model = model(repo)
        advanceUntilIdle()
        model.hunt(); advanceUntilIdle()
        assertEquals(CombatOutcome.Defeat, loaded(model).snapshot.recentHunts.single().combat.outcome)
        assertEquals(100L, loaded(model).player.gold)
        assertEquals(0L, loaded(model).player.currentXp)
        assertEquals(1L, loaded(model).player.defeats)
    }

    private fun loaded(model: HomeViewModel) = model.state.value as HomeUiState.Loaded
    private fun model(repo: HuntRepository) = HomeViewModel(
        LoadHuntUseCase(repo), PerformHuntUseCase(repo), AcknowledgeIntroductionUseCase(repo), clock,
    )
    private inner class MemoryRepository(
        var saved: HuntState = HuntState(Player.newAdventurer().copy(introductionAcknowledged = true)),
    ) : HuntRepository {
        var failLoad = false
        var cancelLoad = false
        var failHunt = false
        var huntCalls = 0
        override suspend fun load(initialPlayer: Player): HuntState {
            if (cancelLoad) throw CancellationException()
            check(!failLoad)
            return saved
        }
        override suspend fun acknowledgeIntroduction(): HuntState {
            saved = saved.copy(player = saved.player.copy(introductionAcknowledged = true))
            return saved
        }
        override suspend fun performHunt(): HuntAttempt {
            huntCalls++
            check(!failHunt)
            return ResolveHuntUseCase(clock, random, GameConfig.Development)(saved).also {
                if (it is HuntAttempt.Completed) saved = it.state
            }
        }
    }
}
