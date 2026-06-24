package com.example.simbionte.core.engine

import com.example.simbionte.db.MemoryBlock
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MemoryDecayEngineTest {

    private lateinit var engine: MemoryDecayEngineImpl

    @Before
    fun setup() {
        engine = MemoryDecayEngineImpl()
    }

    @Test
    fun `calculateDecay fresh block returns full signal`() {
        val block = MemoryBlock(
            id = "1", conversaId = "c1", contextId = null, content = "teste",
            isFromUser = true, timestamp = System.currentTimeMillis(),
            lastReinforcedAt = System.currentTimeMillis(),
            cognitiveSignal = 1.0f
        )
        val decayed = engine.calculateDecay(block)
        assertEquals(1.0f, decayed, 0.01f)
    }

    @Test
    fun `calculateDecay never goes below 0-1`() {
        val block = MemoryBlock(
            id = "1", conversaId = "c1", contextId = null, content = "teste",
            isFromUser = true, timestamp = 0L,
            lastReinforcedAt = 0L,
            cognitiveSignal = 1.0f
        )
        val decayed = engine.calculateDecay(block)
        assertTrue("Decayed signal should be >= 0.1f, got $decayed", decayed >= 0.1f)
    }

    @Test
    fun `reinforce increases signal`() {
        val block = MemoryBlock(
            id = "1", conversaId = "c1", contextId = null, content = "teste",
            isFromUser = true, timestamp = System.currentTimeMillis(),
            cognitiveSignal = 1.0f
        )
        val reinforced = engine.reinforce(block)
        assertTrue("Reinforced signal should be higher", reinforced > 1.0f)
    }

    @Test
    fun `reinforce caps at 5-0`() {
        val block = MemoryBlock(
            id = "1", conversaId = "c1", contextId = null, content = "teste",
            isFromUser = true, timestamp = System.currentTimeMillis(),
            cognitiveSignal = 5.0f
        )
        val reinforced = engine.reinforce(block)
        assertEquals(5.0f, reinforced, 0.01f)
    }
}
