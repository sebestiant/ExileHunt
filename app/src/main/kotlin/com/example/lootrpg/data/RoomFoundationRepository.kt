package com.example.lootrpg.data

import com.example.lootrpg.data.persistence.FoundationDao
import com.example.lootrpg.data.persistence.FoundationMarker
import com.example.lootrpg.foundation.domain.FoundationRepository
import java.time.Instant

internal class RoomFoundationRepository(
    private val dao: FoundationDao,
) : FoundationRepository {
    override suspend fun initialize(initializedAt: Instant) {
        dao.insertIfAbsent(FoundationMarker(initializedAtEpochMillis = initializedAt.toEpochMilli()))
    }
}
