package com.example.simbionte.core.engine

import com.example.simbionte.db.MemoryBlock
import com.example.simbionte.db.MemoryRelation

class MemoryScoringEngineImpl : MemoryScoringEngine {

    override fun calculateScore(block: MemoryBlock, associations: List<MemoryRelation>): Float {
        var score = 1.0f

        when {
            block.content.length > 200 -> score += 1.0f
            block.content.length > 100 -> score += 0.7f
            block.content.length > 50 -> score += 0.4f
            block.content.length > 20 -> score += 0.2f
        }

        val weightSum = associations.sumOf { it.weight.toDouble() }.toFloat()
        score += (associations.size * 0.15f) + (weightSum * 0.1f)

        val tagCount = block.tags?.split(",")?.count { it.isNotBlank() } ?: 0
        if (tagCount >= 3) score += 0.5f
        else if (tagCount >= 1) score += 0.2f

        if (block.cognitiveSignal > 2.0f) score += 0.5f
        else if (block.cognitiveSignal > 1.5f) score += 0.3f

        return score.coerceIn(0.1f, 10.0f)
    }
}
