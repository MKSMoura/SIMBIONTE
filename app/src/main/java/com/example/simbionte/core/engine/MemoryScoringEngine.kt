package com.example.simbionte.core.engine

import com.example.simbionte.db.MemoryBlock
import com.example.simbionte.db.MemoryRelation

interface MemoryScoringEngine {
    /**
     * Calcula o score de relevância inicial e atualiza scores de memórias
     * relacionadas (reforço de memória).
     */
    fun calculateScore(block: MemoryBlock, associations: List<MemoryRelation>): Float
}
