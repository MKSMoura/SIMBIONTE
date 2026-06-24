package com.example.simbionte.core.engine

interface CognitivePresenceLayer {
    fun getCurrentState(): LiveContextState
    fun processPresence(text: String, result: PipelineResult): LiveContextState
}

class CognitivePresenceLayerImpl : CognitivePresenceLayer {
    @Volatile
    private var currentState = LiveContextState()

    @Synchronized
    override fun getCurrentState() = currentState

    @Synchronized
    override fun processPresence(text: String, result: PipelineResult): LiveContextState {
        val now = System.currentTimeMillis()
        val timeDelta = now - currentState.lastUpdateTimestamp
        
        // Momentum: intensidade do fluxo (recompensa entradas rápidas < 15s)
        val newMomentum = if (timeDelta < 15000) {
            (currentState.momentum + 0.15).coerceAtMost(1.0)
        } else {
            (currentState.momentum - 0.05).coerceAtLeast(0.0)
        }

        // Topic Drift: detecta mudança de contexto
        val drift = if (currentState.activeContextId != null && currentState.activeContextId != result.contextId) {
            0.7 // Desvio abrupto
        } else if (result.relations.none { it.relationshipType == RelationType.CONTINUATION }) {
            0.3 // Desvio leve (sem relação de continuidade direta)
        } else {
            0.0 // Fluxo estável
        }

        // Recent Fragments: Janela deslizante de pensamentos
        val newFragments = (currentState.recentFragments + text).takeLast(3)

        currentState = currentState.copy(
            activeContextId = result.contextId,
            recentFragments = newFragments,
            momentum = newMomentum,
            topicDrift = drift,
            lastMeaningfulInput = if (result.relations.any { it.weight > 0.5 }) text else currentState.lastMeaningfulInput,
            lastUpdateTimestamp = now
        )

        return currentState
    }
}
