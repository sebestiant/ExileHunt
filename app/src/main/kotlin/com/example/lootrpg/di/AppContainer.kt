package com.example.lootrpg.di

import android.content.Context
import androidx.room.Room
import com.example.lootrpg.core.domain.RandomProvider
import com.example.lootrpg.core.domain.TimeProvider
import com.example.lootrpg.data.LocalRandomProvider
import com.example.lootrpg.data.LocalTimeProvider
import com.example.lootrpg.data.RoomFoundationRepository
import com.example.lootrpg.data.persistence.LootRpgDatabase
import com.example.lootrpg.foundation.domain.InitializeFoundationUseCase

/** Application-scoped composition root. Only wiring knows concrete dependencies. */
class AppContainer(context: Context) {
    val timeProvider: TimeProvider = LocalTimeProvider()
    val randomProvider: RandomProvider = LocalRandomProvider()

    private val database = Room.databaseBuilder(
        context.applicationContext,
        LootRpgDatabase::class.java,
        "lootrpg.db",
    ).build()

    val initializeFoundation = InitializeFoundationUseCase(
        repository = RoomFoundationRepository(database.foundationDao()),
        timeProvider = timeProvider,
    )
}
