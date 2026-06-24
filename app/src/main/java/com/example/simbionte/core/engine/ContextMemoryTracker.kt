package com.example.simbionte.core.engine

import com.example.simbionte.db.ContextWindow
import com.example.simbionte.db.MemoryBlock

interface ContextMemoryTracker {
    /**
     * Retorna o ID do contexto que está dominando a sessão atual.
     */
    fun getActiveContextId(): String?

    /**
     * Analisa o estado atual das memórias e janelas para atualizar a dominância
     * e aplicar o decay temporal.
     */
    fun track(recentMemories: List<MemoryBlock>, allContexts: List<ContextWindow>)

    /**
     * Verifica se um contexto específico perdeu a relevância (morreu).
     */
    fun isContextStale(contextId: String): Boolean

    /**
     * Fornece o fator de decaimento para um contexto baseado no tempo.
     */
    fun getDecayFactor(contextId: String): Float
}
