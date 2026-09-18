package com.example.lootrpg.hunt.domain

import com.example.lootrpg.core.domain.RandomProvider
import com.example.lootrpg.core.domain.TimeProvider
import com.example.lootrpg.player.domain.CombatStats
import com.example.lootrpg.player.domain.Player
import com.example.lootrpg.player.domain.Progression
import java.time.Instant
import org.junit.Assert.*
import org.junit.Test

class HuntRulesTest {
    private val now = Instant.parse("2026-09-18T12:00:00Z")
    private val player = Player.newAdventurer().copy(introductionAcknowledged = true)

    @Test fun `eligibility respects absolute timestamp including exact boundary`() {
        assertTrue(canHunt(null, now))
        assertFalse(canHunt(now.plusMillis(1), now))
        assertTrue(canHunt(now, now))
        assertTrue(canHunt(now.minusMillis(1), now))
    }

    @Test fun `all weighted rolls map to their exact encounter intervals`() {
        val table = GameDefinitions.wildOutskirts.encounters
        val expected = table.flatMap { entry -> List(entry.weight) { entry.monster } }
        assertEquals(100, expected.size)
        for (roll in 0..99) assertEquals(expected[roll], SelectEncounterUseCase(FixedRandom(roll))(table))
        val otherTable = listOf(EncounterEntry(table.last().monster, 1))
        assertEquals(table.last().monster, SelectEncounterUseCase(FixedRandom())(otherTable))
    }

    @Test fun `combat is reproducible player attacks first and can win or lose`() {
        val combat = CalculateCombatUseCase(FixedRandom())
        val enemy = CombatStats(20, 0, 10)
        assertEquals(CombatResult(CombatOutcome.Victory, 1, 10, 0), combat(player.stats, enemy))
        val loss = combat(CombatStats(1, 0, 10), CombatStats(20, 0, 100))
        assertEquals(CombatResult(CombatOutcome.Defeat, 1, 1, 10), loss)
        assertEquals(loss, combat(CombatStats(1, 0, 10), CombatStats(20, 0, 100)))
    }

    @Test fun `minimum damage and maximum rounds guarantee termination`() {
        val combat = CalculateCombatUseCase(FixedRandom(fraction = 0.0))
        assertEquals(CombatOutcome.Victory, combat(CombatStats(0, 999, 3), CombatStats(0, 999, 3)).outcome)
        val capped = combat(CombatStats(0, 999, 1000), CombatStats(0, 999, 1000))
        assertTrue(capped.roundLimitReached)
        assertEquals(200, capped.rounds)
        assertEquals(CombatOutcome.Defeat, capped.outcome)
    }

    @Test fun `all specified monsters lose even against worst-case level one damage`() {
        // Explicitly documents the approved balancing limitation rather than silently changing stats.
        for (entry in GameDefinitions.wildOutskirts.encounters) {
            var attack = 0
            val rng = object : RandomProvider {
                override fun nextInt(boundExclusive: Int) = 0
                override fun nextDouble() = if (attack++ % 2 == 0) 0.0 else 0.999999
            }
            assertEquals(entry.monster.name, CombatOutcome.Victory,
                CalculateCombatUseCase(rng)(player.stats, entry.monster.stats).outcome)
        }
    }

    @Test fun `victory awards reward endpoints and persists a future cooldown in result`() {
        for (maximumGold in listOf(false, true)) {
            val rng = object : RandomProvider {
                override fun nextInt(boundExclusive: Int) = if (maximumGold && boundExclusive != 100) boundExclusive - 1 else 0
                override fun nextDouble() = 0.5
            }
            val result = ResolveHuntUseCase(TimeProvider { now }, rng, GameConfig.Development)(HuntState(player)) as HuntAttempt.Completed
            assertEquals(12, result.result.experienceGained)
            assertEquals(if (maximumGold) 8L else 4L, result.result.goldGained)
            assertEquals(12L, result.state.player.currentXp)
            assertEquals(1L, result.state.player.victories)
            assertEquals(now.plusSeconds(10), result.state.player.nextHuntAt)
            assertEquals(result.result, result.state.recentHunts.single())
        }
    }

    @Test fun `defeat grants nothing and still consumes cooldown`() {
        val weak = player.copy(stats = CombatStats(1, 0, 1))
        val result = resolve(HuntState(weak)) as HuntAttempt.Completed
        assertEquals(CombatOutcome.Defeat, result.result.combat.outcome)
        assertEquals(0L, result.result.experienceGained)
        assertEquals(0L, result.result.goldGained)
        assertEquals(100L, result.state.player.gold)
        assertEquals(1L, result.state.player.defeats)
        assertEquals(now.plusSeconds(10), result.state.player.nextHuntAt)
    }

    @Test fun `cooldown and introduction rejection consume no randomness`() {
        val failRandom = object : RandomProvider {
            override fun nextInt(boundExclusive: Int): Int = error("Must not roll")
            override fun nextDouble(): Double = error("Must not roll")
        }
        val resolver = ResolveHuntUseCase(TimeProvider { now }, failRandom, GameConfig.Development)
        assertTrue(resolver(HuntState(player.copy(nextHuntAt = now.plusSeconds(1)))) is HuntAttempt.CoolingDown)
        assertTrue(resolver(HuntState(Player.newAdventurer())) is HuntAttempt.IntroductionRequired)
    }

    @Test fun `level up carries XP and increases each stat`() {
        val advanced = Progression.award(player.copy(currentXp = 95), 12, 4)
        assertEquals(2, advanced.level)
        assertEquals(7L, advanced.currentXp)
        assertEquals(104L, advanced.gold)
        assertEquals(CombatStats(12, 6, 110), advanced.stats)
        val hunt = resolve(HuntState(player.copy(currentXp = 95))) as HuntAttempt.Completed
        assertEquals(LevelUp(1, 2, player.stats, advanced.stats), hunt.result.levelUp)
    }

    @Test fun `large rewards cross all thresholds and retain XP at level five`() {
        val advanced = Progression.award(player, 900, 0)
        assertEquals(5, advanced.level)
        assertEquals(100L, advanced.currentXp)
        assertEquals(CombatStats(18, 9, 140), advanced.stats)
        assertNull(Progression.requiredXp(5))
        assertEquals(150L, Progression.award(advanced, 50, 0).currentXp)
        assertEquals(player, Progression.award(player, 0, 0))
    }

    @Test fun `history keeps latest ten and every encounter starts at full health`() {
        var state = HuntState(player)
        repeat(12) {
            state = (resolve(state.copy(player = state.player.copy(nextHuntAt = null))) as HuntAttempt.Completed).state
        }
        assertEquals(10, state.recentHunts.size)
        assertEquals((12L downTo 3).toList(), state.recentHunts.map { it.sequence })
        assertEquals(12L, state.player.totalHunts)
        assertEquals(0L, state.player.defeats)
    }

    @Test fun `release default is fifteen minutes and development is ten seconds`() {
        assertEquals(900L, GameConfig().huntCooldown.seconds)
        assertEquals(10L, GameConfig.Development.huntCooldown.seconds)
    }

    private fun resolve(state: HuntState) =
        ResolveHuntUseCase(TimeProvider { now }, FixedRandom(), GameConfig.Development)(state)

    private class FixedRandom(private val roll: Int = 0, private val fraction: Double = 0.5) : RandomProvider {
        override fun nextInt(boundExclusive: Int) = roll.also { require(it in 0 until boundExclusive) }
        override fun nextDouble() = fraction
    }
}
