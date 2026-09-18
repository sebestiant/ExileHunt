package com.example.lootrpg.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lootrpg.core.domain.TimeProvider
import com.example.lootrpg.hunt.domain.*
import java.time.Duration
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val loadHunt: LoadHuntUseCase,
    private val performHunt: PerformHuntUseCase,
    private val acknowledgeIntroduction: AcknowledgeIntroductionUseCase,
    private val clock: TimeProvider,
) : ViewModel() {
    private val mutableState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val state: StateFlow<HomeUiState> = mutableState.asStateFlow()
    private var operation: Job? = null

    init { initialize() }

    fun retry() {
        if (mutableState.value == HomeUiState.Error) initialize()
    }

    private fun initialize() {
        if (operation?.isActive == true) return
        operation = viewModelScope.launch {
            mutableState.value = HomeUiState.Loading
            try { mutableState.value = display(loadHunt()) }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) { mutableState.value = HomeUiState.Error }
        }
    }

    fun acknowledgeOpening() = runAction(HuntActionError.Introduction) {
        mutableState.value = display(acknowledgeIntroduction())
    }

    fun hunt() {
        refreshClock()
        val loaded = mutableState.value as? HomeUiState.Loaded ?: return
        if (!loaded.huntAvailable) return
        runAction(HuntActionError.Hunt) {
            mutableState.value = loaded.copy(busy = true, phase = EncounterPhase.Searching)
            when (val attempt = performHunt()) {
                is HuntAttempt.Completed -> {
                    // The result is already durable. Delays only control its presentation.
                    mutableState.value = display(attempt.state).copy(busy = true, phase = EncounterPhase.Searching)
                    delay(650)
                    mutableState.value = display(attempt.state).copy(busy = true, phase = EncounterPhase.Encounter)
                    delay(650)
                    mutableState.value = display(attempt.state)
                }
                is HuntAttempt.CoolingDown -> mutableState.value = display(attempt.state)
                is HuntAttempt.IntroductionRequired -> mutableState.value = display(attempt.state)
            }
        }
    }

    private fun runAction(error: HuntActionError, action: suspend () -> Unit) {
        val loaded = mutableState.value as? HomeUiState.Loaded ?: return
        if (loaded.busy || operation?.isActive == true) return
        mutableState.value = loaded.copy(busy = true, actionError = null)
        operation = viewModelScope.launch {
            try { action() }
            catch (cancelled: CancellationException) { throw cancelled }
            catch (_: Exception) {
                // Keep the last known good snapshot after a failed atomic operation.
                mutableState.value = loaded.copy(actionError = error)
            } finally {
                (mutableState.value as? HomeUiState.Loaded)?.let {
                    mutableState.value = it.copy(busy = false, phase = EncounterPhase.Idle)
                }
                refreshClock()
            }
        }
    }

    /** Called while the Activity is STARTED; foreground re-entry immediately resamples absolute time. */
    suspend fun updateClockWhileActive() {
        while (currentCoroutineContext().isActive) {
            refreshClock()
            delay(1000)
        }
    }

    fun refreshClock() {
        (mutableState.value as? HomeUiState.Loaded)?.let {
            mutableState.value = it.copy(remainingSeconds = remaining(it.snapshot))
        }
    }

    private fun display(snapshot: HuntState) = HomeUiState.Loaded(snapshot, remaining(snapshot))

    private fun remaining(snapshot: HuntState): Long {
        val target = snapshot.player.nextHuntAt ?: return 0
        val now = clock.now()
        if (!now.isBefore(target)) return 0
        val duration = Duration.between(now, target)
        return duration.seconds + if (duration.nano > 0) 1 else 0
    }
}
