package com.example.lootrpg.core.domain

import java.time.Instant
import org.junit.Assert.assertEquals
import org.junit.Test

class TimeProviderTest {
    @Test
    fun `time can be advanced deterministically without device time`() {
        var now = Instant.EPOCH
        val clock: TimeProvider = TimeProvider { now }
        assertEquals(Instant.EPOCH, clock.now())
        now = now.plusSeconds(1)
        assertEquals(Instant.ofEpochSecond(1), clock.now())
    }
}
