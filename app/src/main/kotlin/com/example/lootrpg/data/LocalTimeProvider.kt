package com.example.lootrpg.data

import com.example.lootrpg.core.domain.TimeProvider
import java.time.Clock
import java.time.Instant

class LocalTimeProvider(private val clock: Clock = Clock.systemUTC()) : TimeProvider {
    override fun now(): Instant = clock.instant()
}
