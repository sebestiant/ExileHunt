package com.example.lootrpg.hunt.domain

import java.time.Duration

/** Authoritative cooldown configuration. Wiring selects Development only for debug builds. */
data class GameConfig(val huntCooldown: Duration = Duration.ofMinutes(15)) {
    init { require(!huntCooldown.isNegative && !huntCooldown.isZero) }
    companion object {
        val Production = GameConfig()
        val Development = GameConfig(Duration.ofSeconds(10))
    }
}
