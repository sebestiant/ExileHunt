package com.example.lootrpg.foundation.domain

import com.example.lootrpg.core.domain.TimeProvider
import java.time.Instant
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Test

class InitializeFoundationUseCaseTest {
    @Test
    fun `uses injected time and a repository without Android`() = runTest {
        val expected = Instant.parse("2026-09-18T12:00:00Z")
        var recorded: Instant? = null
        val repository = FoundationRepository { recorded = it }

        InitializeFoundationUseCase(repository, TimeProvider { expected })()

        assertEquals(expected, recorded)
    }

    @Test
    fun `reads clock on each invocation`() = runTest {
        var now = Instant.EPOCH
        val recorded = mutableListOf<Instant>()
        val useCase = InitializeFoundationUseCase(
            FoundationRepository { recorded.add(it) },
            TimeProvider { now },
        )
        useCase()
        now = now.plusSeconds(5)
        useCase()
        assertEquals(listOf(Instant.EPOCH, Instant.EPOCH.plusSeconds(5)), recorded)
    }

    @Test
    fun `repository failures propagate to caller`() = runTest {
        val failure = IllegalStateException("Unavailable")
        val useCase = InitializeFoundationUseCase(
            FoundationRepository { throw failure },
            TimeProvider { Instant.EPOCH },
        )
        try {
            useCase()
            throw AssertionError("Expected failure")
        } catch (actual: IllegalStateException) {
            assertSame(failure, actual)
        }
    }
}
