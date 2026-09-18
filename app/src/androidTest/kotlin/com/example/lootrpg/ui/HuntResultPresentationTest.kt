package com.example.lootrpg.ui

import android.view.accessibility.AccessibilityNodeInfo
import androidx.activity.compose.setContent
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.lootrpg.MainActivity
import com.example.lootrpg.hunt.domain.CombatOutcome
import com.example.lootrpg.hunt.domain.CombatResult
import com.example.lootrpg.hunt.domain.HuntResult
import com.example.lootrpg.hunt.domain.LevelUp
import com.example.lootrpg.player.domain.CombatStats
import java.time.Instant
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

/** Rendering fixtures exercise otherwise unreachable defeat without changing live game balance. */
@RunWith(AndroidJUnit4::class)
class HuntResultPresentationTest {
    @Test fun defeatAndLevelUpAreClearlyRendered() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val defeat = HuntResult(1, "fractured-boar", "Fractured Boar", 3, "Test fixture",
            CombatResult(CombatOutcome.Defeat, 12, 70, 100), 0, 0,
            Instant.EPOCH, Instant.EPOCH.plusSeconds(10), null)
        ActivityScenario.launch(MainActivity::class.java).use { scenario ->
            scenario.onActivity { activity ->
                activity.setContent { ExileHuntTheme { ScreenColumn { HuntResultCard(defeat) } } }
            }
            instrumentation.waitForIdleSync()
            instrumentation.uiAutomation.waitForIdle(250, 5000)
            val lossText = text(instrumentation.uiAutomation.rootInActiveWindow)
            assertTrue(lossText, lossText.contains("DEFEAT"))
            assertTrue(lossText, lossText.contains("forced you back toward Greyhaven"))
            assertTrue(lossText, lossText.contains("+0 XP"))
            scenario.onActivity { activity ->
                activity.setContent { ExileHuntTheme { ScreenColumn {
                    HuntResultCard(defeat.copy(
                        combat = CombatResult(CombatOutcome.Victory, 12, 75, 70),
                        experienceGained = 32, goldGained = 15,
                        levelUp = LevelUp(1, 2, CombatStats(10, 5, 100), CombatStats(12, 6, 110)),
                    ))
                } } }
            }
            instrumentation.waitForIdleSync()
            instrumentation.uiAutomation.waitForIdle(250, 5000)
            val winText = text(instrumentation.uiAutomation.rootInActiveWindow)
            assertTrue(winText, winText.contains("VICTORY"))
            assertTrue(winText, winText.contains("LEVEL UP! Level 2"))
        }
    }

    private fun text(node: AccessibilityNodeInfo?): String {
        if (node == null) return ""
        return buildString {
            append(node.text?.toString().orEmpty())
            for (index in 0 until node.childCount) { append(' '); append(text(node.getChild(index))) }
        }
    }
}
