package com.example.lootrpg.foundation.domain

import com.example.lootrpg.core.domain.TimeProvider

class InitializeFoundationUseCase(
    private val repository: FoundationRepository,
    private val timeProvider: TimeProvider,
) {
    suspend operator fun invoke() {
        repository.initialize(timeProvider.now())
    }
}
