package com.example.lootrpg.data.persistence

import androidx.room.Entity
import androidx.room.PrimaryKey

/** Temporary singleton used solely to validate the Room integration. */
@Entity(tableName = "foundation_marker")
internal data class FoundationMarker(
    @PrimaryKey val id: Int = 1,
    val initializedAtEpochMillis: Long,
)
