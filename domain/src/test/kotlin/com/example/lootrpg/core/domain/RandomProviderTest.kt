package com.example.lootrpg.core.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class RandomProviderTest {
    @Test
    fun `consumer accepts deterministic replacement including boundary values`() {
        val random: RandomProvider = SequenceRandomProvider(listOf(0, 9), listOf(0.0, 0.999))
        assertEquals(listOf(0, 9), List(2) { random.nextInt(10) })
        assertEquals(listOf(0.0, 0.999), List(2) { random.nextDouble() })
    }

    private class SequenceRandomProvider(ints: List<Int>, doubles: List<Double>) : RandomProvider {
        private val integers = ints.iterator()
        private val fractions = doubles.iterator()

        override fun nextInt(boundExclusive: Int): Int {
            require(boundExclusive > 0)
            return integers.next().also { require(it in 0 until boundExclusive) }
        }

        override fun nextDouble(): Double = fractions.next().also {
            require(it >= 0.0 && it < 1.0)
        }
    }
}
