package com.example.lootrpg.data

import android.content.Context
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.lootrpg.data.persistence.LootRpgDatabase
import com.example.lootrpg.player.domain.CombatStats
import com.example.lootrpg.player.domain.LoadPlayerUseCase
import com.example.lootrpg.player.domain.Player
import com.example.lootrpg.player.domain.PlayerId
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PlayerPersistenceTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val name = "player-test.db"

    @Before fun cleanBefore() { context.deleteDatabase(name) }
    @After fun cleanAfter() { context.deleteDatabase(name) }

    @Test
    fun firstLaunchCreatesDefaultPlayerEvenWithConcurrentRequests() = runTest {
        val database = open()
        try {
            val load = LoadPlayerUseCase(RoomPlayerRepository(database.playerDao()))
            val players = List(8) { async { load() } }.awaitAll()
            assertEquals(List(8) { Player.newAdventurer() }, players)
            database.openHelper.readableDatabase.query("SELECT COUNT(*) FROM player").use {
                it.moveToFirst()
                assertEquals(1, it.getInt(0))
            }
        } finally { database.close() }
    }

    @Test
    fun existingPlayerSurvivesDatabaseAndRepositoryRecreation() = runTest {
        val existing = Player(
            PlayerId("persisted-id"), "Wayfarer", 1, 23, 147, CombatStats(12, 7, 110),
        )
        val first = open()
        try {
            RoomPlayerRepository(first.playerDao()).getOrCreate(existing)
        } finally { first.close() }
        val reopened = open()
        try {
            val load = LoadPlayerUseCase(RoomPlayerRepository(reopened.playerDao()))
            assertEquals(existing, load())
            assertEquals(existing, load())
        } finally { reopened.close() }
    }

    @Test
    fun versionOneSchemaMigratesAndInitializesPlayerWithoutDestructiveFallback() = runTest {
        // Recreate the actual exported v1 schema, including Room's identity hash.
        val assets = InstrumentationRegistry.getInstrumentation().context.assets
        val schema = assets.open(
            "com.example.lootrpg.data.persistence.LootRpgDatabase/1.json",
        ).bufferedReader().use { JSONObject(it.readText()).getJSONObject("database") }
        context.openOrCreateDatabase(name, Context.MODE_PRIVATE, null).use { old ->
            val entities = schema.getJSONArray("entities")
            for (index in 0 until entities.length()) {
                val entity = entities.getJSONObject(index)
                old.execSQL(entity.getString("createSql").replace("\${TABLE_NAME}", entity.getString("tableName")))
            }
            val setup = schema.getJSONArray("setupQueries")
            for (index in 0 until setup.length()) old.execSQL(setup.getString(index))
            old.execSQL("INSERT INTO foundation_marker VALUES (1, 1234)")
            old.version = 1
        }
        val migrated = open()
        try {
            assertEquals(Player.newAdventurer(), LoadPlayerUseCase(RoomPlayerRepository(migrated.playerDao()))())
            val sqlite = migrated.openHelper.readableDatabase
            assertEquals(3, sqlite.version)
            sqlite.query("SELECT name FROM sqlite_master WHERE name = 'foundation_marker'").use {
                assertFalse(it.moveToFirst())
            }
        } finally { migrated.close() }
    }

    private fun open() = Room.databaseBuilder(context, LootRpgDatabase::class.java, name)
        .addMigrations(LootRpgDatabase.MIGRATION_1_2, LootRpgDatabase.MIGRATION_2_3).build()
}
