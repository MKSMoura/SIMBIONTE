package com.example.simbionte.core.engine

import com.example.simbionte.db.MemoryBlock
import kotlin.math.exp
import kotlin.math.max

class MemoryDecayEngineImpl : MemoryDecayEngine {

    private val HALFLIFE = 24 * 60 * 60 * 1000.0
    private val DECAY_CONSTANT = 0.693 / HALFLIFE

    override fun calculateDecay(block: MemoryBlock): Float {
        val timePassed = System.currentTimeMillis() - block.lastReinforcedAt
        val decayedSignal = block.cognitiveSignal * exp(-DECAY_CONSTANT * timePassed)
        return max(0.1f, decayedSignal.toFloat())
    }

    override fun reinforce(block: MemoryBlock): Float {
        return (block.cognitiveSignal + 0.5f).coerceAtMost(5.0f)
    }
}
