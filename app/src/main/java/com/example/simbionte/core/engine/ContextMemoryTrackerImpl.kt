package com.example.simbionte.core.engine

import com.example.simbionte.db.ContextWindow
import com.example.simbionte.db.MemoryBlock
import kotlin.math.max

class ContextMemoryTrackerImpl(private val decayEngine: MemoryDecayEngine = MemoryDecayEngineImpl()) : ContextMemoryTracker {
    private var activeContextId: String? = null
    private val decayRates = mutableMapOf<String, Float>()
    
    // Configurações de decaimento
    private val STALE_THRESHOLD = 0.15f
    private val DECAY_MILLIS = 60 * 60 * 1000 // 1 hora para decaimento significativo

    override fun getActiveContextId(): String? = activeContextId

    override fun track(recentMemories: List<MemoryBlock>, allContexts: List<ContextWindow>) {
        val now = System.currentTimeMillis()
        
        // 1. Calcular decay para todos os contextos
        allContexts.forEach { context ->
            val timePassed = now - context.updatedAt
            val temporalFactor = max(0f, 1f - (timePassed.toFloat() / (DECAY_MILLIS * 2)))
            decayRates[context.contextId] = temporalFactor
        }

        // 2. Identificar Contexto Dominante
        // Score = Relevância * Sinal Cognitivo * Fator Temporal
        activeContextId = allContexts
            .filter { !isContextStale(it.contextId) }
            .maxByOrNull { 
                it.relevanceScore * it.cognitiveSignal * (decayRates[it.contextId] ?: 1f)
            }
            ?.contextId
            
        // 3. Se não houver contexto dominante claro, tenta usar o mais recente da memória
        if (activeContextId == null) {
            activeContextId = recentMemories.lastOrNull()?.contextId
        }
    }

    override fun isContextStale(contextId: String): Boolean {
        return (decayRates[contextId] ?: 0f) < STALE_THRESHOLD
    }

    override fun getDecayFactor(contextId: String): Float {
        return decayRates[contextId] ?: 1f
    }
}
