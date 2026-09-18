package com.example.lootrpg.core.domain

interface RandomProvider {
    /** Uniform integer in [0, boundExclusive). The bound must be positive. */
    fun nextInt(boundExclusive: Int): Int

    /** Uniform value in [0.0, 1.0). Not intended for security or identifiers. */
    fun nextDouble(): Double
}
