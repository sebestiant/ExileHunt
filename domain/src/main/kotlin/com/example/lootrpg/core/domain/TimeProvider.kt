package com.example.lootrpg.core.domain

import java.time.Instant

fun interface TimeProvider {
    /** Current absolute time. Future cooldowns store instants, never countdown state. */
    fun now(): Instant
}
