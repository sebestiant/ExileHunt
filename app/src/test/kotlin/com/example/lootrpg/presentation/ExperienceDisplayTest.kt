package com.example.lootrpg.presentation

import com.example.lootrpg.player.domain.Player
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ExperienceDisplayTest {
    @Test
    fun `level one uses the specified display target`() {
        val display = ExperienceDisplay.forPlayer(Player.newAdventurer().copy(currentXp = 25))
        assertEquals(100L, display.required)
        assertEquals(0.25f, display.fraction)
    }

    @Test
    fun `unknown requirements are not invented and progress is bounded`() {
        val unknown = ExperienceDisplay.forPlayer(Player.newAdventurer().copy(level = 5))
        assertNull(unknown.required)
        assertEquals(0f, unknown.fraction)
        assertEquals(1f, ExperienceDisplay(101, 100).fraction)
    }
}
