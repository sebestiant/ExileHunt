package com.example.lootrpg.data

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.lootrpg.core.domain.RandomProvider
import com.example.lootrpg.core.domain.TimeProvider
import com.example.lootrpg.data.persistence.LootRpgDatabase
import com.example.lootrpg.hunt.domain.*
import com.example.lootrpg.player.domain.Player
import java.time.Instant
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runTest
import org.json.JSONObject
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HuntPersistenceTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val name = "hunt-test.db"
    private var now = Instant.parse("2026-09-18T12:00:00Z")
    private val random = object : RandomProvider {
        override fun nextInt(boundExclusive: Int) = 0
        override fun nextDouble() = 0.5
    }
    @Before fun before() { context.deleteDatabase(name) }
    @After fun after() { context.deleteDatabase(name) }

    @Test fun rewardsCooldownIntroductionAndHistorySurviveReopening() = runTest {
        val db = open()
        val saved: HuntState
        try {
            val repo = repository(db)
            repo.load(Player.newAdventurer().copy(currentXp = 95))
            repo.acknowledgeIntroduction()
            saved = (repo.performHunt() as HuntAttempt.Completed).state
            assertEquals(2, saved.player.level)
            assertEquals(7L, saved.player.currentXp)
            assertEquals(104L, saved.player.gold)
        } finally { db.close() }
        val reopened = open()
        try {
            val repo = repository(reopened)
            assertEquals(saved, repo.load(Player.newAdventurer()))
            assertTrue(repo.performHunt() is HuntAttempt.CoolingDown)
            now = now.plusSeconds(10)
            assertTrue(repo.performHunt() is HuntAttempt.Completed)
        } finally { reopened.close() }
    }

    @Test fun simultaneousRequestsCommitOnlyOneHuntAndPruneToTen() = runTest {
        val db = open()
        try {
            val repo = repository(db)
            repo.load(Player.newAdventurer())
            repo.acknowledgeIntroduction()
            val results = List(8) { async { repository(db).performHunt() } }.awaitAll()
            assertEquals(1, results.count { it is HuntAttempt.Completed })
            assertEquals(7, results.count { it is HuntAttempt.CoolingDown })
            repeat(11) { now = now.plusSeconds(10); repo.performHunt() }
            val state = repo.load(Player.newAdventurer())
            assertEquals(12L, state.player.totalHunts)
            assertEquals(10, state.recentHunts.size)
            db.openHelper.readableDatabase.query("SELECT COUNT(*) FROM recent_hunt").use {
                it.moveToFirst(); assertEquals(10, it.getInt(0))
            }
        } finally { db.close() }
    }

    @Test fun defeatFixturePersistsNoRewardsAndCountsTheHunt() = runTest {
        val db = open()
        try {
            val repo = repository(db)
            val weak = Player.newAdventurer().copy(
                stats = com.example.lootrpg.player.domain.CombatStats(1, 0, 1), introductionAcknowledged = true,
            )
            repo.load(weak)
            val result = repo.performHunt() as HuntAttempt.Completed
            assertEquals(CombatOutcome.Defeat, result.result.combat.outcome)
            assertEquals(0L, result.state.player.currentXp)
            assertEquals(100L, result.state.player.gold)
            assertEquals(1L, result.state.player.defeats)
            assertNotNull(result.state.player.nextHuntAt)
            assertEquals(result.state, repo.load(Player.newAdventurer()))
        } finally { db.close() }
    }

    @Test fun historyWriteFailureRollsBackEveryPlayerChange() = runTest {
        val db = open()
        try {
            val repo = repository(db)
            repo.load(Player.newAdventurer().copy(currentXp = 95))
            val before = repo.acknowledgeIntroduction()
            db.openHelper.writableDatabase.execSQL("""
                CREATE TRIGGER reject_hunt BEFORE INSERT ON recent_hunt
                BEGIN SELECT RAISE(ABORT, 'Injected history failure'); END
            """.trimIndent())
            try { repo.performHunt(); fail("Expected write failure") } catch (_: android.database.sqlite.SQLiteException) { }
            assertEquals(before, repo.load(Player.newAdventurer()))
            db.openHelper.writableDatabase.execSQL("DROP TRIGGER reject_hunt")
            assertTrue(repo.performHunt() is HuntAttempt.Completed)
        } finally { db.close() }
    }

    @Test fun versionTwoMigrationPreservesExistingPlayerAndAddsEmptyHuntState() = runTest {
        val assets = InstrumentationRegistry.getInstrumentation().context.assets
        val schema = assets.open("com.example.lootrpg.data.persistence.LootRpgDatabase/2.json")
            .bufferedReader().use { JSONObject(it.readText()).getJSONObject("database") }
        context.openOrCreateDatabase(name, 0, null).use { old ->
            val entities = schema.getJSONArray("entities")
            for (index in 0 until entities.length()) {
                val entity = entities.getJSONObject(index)
                old.execSQL(entity.getString("createSql").replace("\${TABLE_NAME}", entity.getString("tableName")))
            }
            val setup = schema.getJSONArray("setupQueries")
            for (index in 0 until setup.length()) old.execSQL(setup.getString(index))
            old.execSQL("INSERT INTO player VALUES (1, 'local-player', 'Veteran', 2, 37, 231, 12, 6, 110)")
            old.version = 2
        }
        val db = open()
        try {
            val state = repository(db).load(Player.newAdventurer())
            assertEquals("Veteran", state.player.name)
            assertEquals(2, state.player.level)
            assertEquals(37L, state.player.currentXp)
            assertEquals(231L, state.player.gold)
            assertNull(state.player.nextHuntAt)
            assertFalse(state.player.introductionAcknowledged)
            assertEquals(0L, state.player.totalHunts)
            assertTrue(state.recentHunts.isEmpty())
        } finally { db.close() }
    }

    private fun repository(db: LootRpgDatabase) = RoomHuntRepository(
        db, ResolveHuntUseCase(TimeProvider { now }, random, GameConfig.Development),
    )
    private fun open() = Room.databaseBuilder(context, LootRpgDatabase::class.java, name)
        .addMigrations(LootRpgDatabase.MIGRATION_1_2, LootRpgDatabase.MIGRATION_2_3).build()
}
