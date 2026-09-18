package com.example.lootrpg.data

import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalProvidersTest {
    @Test
    fun `local clock returns the supplied instant regardless of timezone`() {
        val instant = Instant.parse("2026-09-18T12:00:00Z")
        val provider = LocalTimeProvider(Clock.fixed(instant, ZoneOffset.ofHours(8)))
        assertEquals(instant, provider.now())
    }

    @Test
    fun `seeded local random is reproducible and respects bounds`() {
        val first = LocalRandomProvider(Random(42))
        val second = LocalRandomProvider(Random(42))
        repeat(100) {
            val integer = first.nextInt(7)
            assertEquals(integer, second.nextInt(7))
            assertTrue(integer in 0 until 7)
            val fraction = first.nextDouble()
            assertEquals(fraction, second.nextDouble(), 0.0)
            assertTrue(fraction >= 0.0 && fraction < 1.0)
        }
        assertEquals(0, first.nextInt(1))
    }

    @Test
    fun `random rejects nonpositive bounds`() {
        val provider = LocalRandomProvider(Random(0))
        assertThrows(IllegalArgumentException::class.java) { provider.nextInt(0) }
        assertThrows(IllegalArgumentException::class.java) { provider.nextInt(-1) }
    }
}
