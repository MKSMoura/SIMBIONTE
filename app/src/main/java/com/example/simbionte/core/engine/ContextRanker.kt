package com.example.simbionte.core.engine

import com.example.simbionte.db.MemoryBlock

interface ContextRanker {
    /**
     * Identifica os contextos mais relevantes/dominantes no momento
     * para sugerir conexões de "Retorno" (RETURN).
     */
    suspend fun rankTopContexts(allMemories: List<MemoryBlock>): List<String>
}
