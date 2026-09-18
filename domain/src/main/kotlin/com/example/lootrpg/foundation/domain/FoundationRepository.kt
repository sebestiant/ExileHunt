package com.example.lootrpg.foundation.domain

import java.time.Instant

/** Temporary integration seam; this is not player state or a game repository. */
fun interface FoundationRepository {
    /** Persist the first initialization time if absent. Repeated calls must preserve it. */
    suspend fun initialize(initializedAt: Instant)
}
