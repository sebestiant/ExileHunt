package com.example.lootrpg.presentation

import com.example.lootrpg.core.domain.TimeProvider
import com.example.lootrpg.foundation.domain.FoundationRepository
import com.example.lootrpg.foundation.domain.InitializeFoundationUseCase
import java.time.Instant
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
        val model = model(FoundationRepository { calls++ })
        assertEquals(HomeUiState.Loading, model.state.value)
        advanceUntilIdle()
        assertEquals(HomeUiState.Ready, model.state.value)
        model.retry()
        advanceUntilIdle()
        assertEquals(1, calls)
    }

    @Test
    fun `failure can be retried without duplicate requests`() = runTest(dispatcher) {
        var calls = 0
        val model = model(FoundationRepository {
            if (++calls == 1) throw IllegalStateException("Unavailable")
        })
        advanceUntilIdle()
        assertEquals(HomeUiState.Error, model.state.value)
        model.retry()
        model.retry()
        advanceUntilIdle()
        assertEquals(HomeUiState.Ready, model.state.value)
        assertEquals(2, calls)
    }

    @Test
    fun `cancellation is not converted into an error`() = runTest(dispatcher) {
        val model = model(FoundationRepository { throw CancellationException() })
        advanceUntilIdle()
        assertEquals(HomeUiState.Loading, model.state.value)
    }

    private fun model(repository: FoundationRepository) = HomeViewModel(
        InitializeFoundationUseCase(repository, TimeProvider { Instant.EPOCH }),
    )
}
