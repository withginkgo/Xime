package com.kingzcheung.xime.ui

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class KeyboardModeTest {

    @Test
    fun `keyboard mode contains expected values in order`() {
        val modes = KeyboardMode.entries

        assertEquals(4, modes.size)
        assertEquals(KeyboardMode.FULL, modes[0])
        assertEquals(KeyboardMode.NUMBER, modes[1])
        assertEquals(KeyboardMode.SYMBOL, modes[2])
        assertEquals(KeyboardMode.NINEKEY, modes[3])
    }

    @Test
    fun `valueOf resolves all keyboard mode names`() {
        assertEquals(KeyboardMode.FULL, KeyboardMode.valueOf("FULL"))
        assertEquals(KeyboardMode.NUMBER, KeyboardMode.valueOf("NUMBER"))
        assertEquals(KeyboardMode.SYMBOL, KeyboardMode.valueOf("SYMBOL"))
        assertEquals(KeyboardMode.NINEKEY, KeyboardMode.valueOf("NINEKEY"))
    }

    @Test
    fun `mode names are stable for persistence`() {
        val persistedNames = KeyboardMode.entries.map { it.name }

        assertTrue(persistedNames.containsAll(listOf("FULL", "NUMBER", "SYMBOL", "NINEKEY")))
    }
}
