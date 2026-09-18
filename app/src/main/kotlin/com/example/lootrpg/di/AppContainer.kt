package com.example.lootrpg.di

import android.content.Context
import androidx.room.Room
import com.example.lootrpg.core.domain.RandomProvider
import com.example.lootrpg.core.domain.TimeProvider
import com.example.lootrpg.data.LocalRandomProvider
import com.example.lootrpg.data.LocalTimeProvider
import com.example.lootrpg.data.persistence.LootRpgDatabase
import com.example.lootrpg.BuildConfig
import com.example.lootrpg.data.RoomHuntRepository
import com.example.lootrpg.hunt.domain.*

/** Application-scoped composition root. Only wiring knows concrete dependencies. */
class AppContainer(context: Context) {
    val timeProvider: TimeProvider = LocalTimeProvider()
    val randomProvider: RandomProvider = LocalRandomProvider()

    private val database = Room.databaseBuilder(
        context.applicationContext,
        LootRpgDatabase::class.java,
        "lootrpg.db",
    ).addMigrations(LootRpgDatabase.MIGRATION_1_2, LootRpgDatabase.MIGRATION_2_3).build()

    private val huntRepository = RoomHuntRepository(database, ResolveHuntUseCase(
        timeProvider, randomProvider, if (BuildConfig.DEBUG) GameConfig.Development else GameConfig.Production,
    ))
    val loadHunt = LoadHuntUseCase(huntRepository)
    val performHunt = PerformHuntUseCase(huntRepository)
    val acknowledgeIntroduction = AcknowledgeIntroductionUseCase(huntRepository)
}
