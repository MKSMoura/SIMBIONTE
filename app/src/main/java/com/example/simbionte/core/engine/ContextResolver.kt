package com.example.simbionte.core.engine

import com.example.simbionte.db.MemoryBlock

interface ContextResolver {
    /**
     * Resolve qual Context ID deve ser associado a este texto.
     * @param activeContextId O contexto atualmente promovido pelo Tracker.
     */
    suspend fun resolveContext(
        text: String, 
        recentMemories: List<MemoryBlock>, 
        activeContextId: String?
    ): String
}
