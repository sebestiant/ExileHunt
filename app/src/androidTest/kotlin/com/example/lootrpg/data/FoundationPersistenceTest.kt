package com.example.lootrpg.data

import android.content.Context
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.lootrpg.data.persistence.LootRpgDatabase
import java.time.Instant
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FoundationPersistenceTest {
    @Test
    fun initializationSurvivesReopenAndPreservesFirstTimestamp() = runTest {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val name = "foundation-test.db"
        context.deleteDatabase(name)
        try {
            val firstDatabase = open(context, name)
            try {
                RoomFoundationRepository(firstDatabase.foundationDao()).initialize(Instant.ofEpochMilli(1234))
            } finally {
                firstDatabase.close()
            }
            val reopenedDatabase = open(context, name)
            try {
                RoomFoundationRepository(reopenedDatabase.foundationDao()).initialize(Instant.ofEpochMilli(5678))
                assertEquals(1234L, reopenedDatabase.foundationDao().read()?.initializedAtEpochMillis)
            } finally {
                reopenedDatabase.close()
            }
        } finally {
            context.deleteDatabase(name)
        }
    }

    private fun open(context: Context, name: String): LootRpgDatabase =
        Room.databaseBuilder(context, LootRpgDatabase::class.java, name).build()
}
