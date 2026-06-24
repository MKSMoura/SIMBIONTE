package com.example.simbionte.core.engine

/**
 * Representa a "consciência operacional" do Symbionte.
 * Mantém o estado cognitivo ativo durante a interação em tempo real.
 */
data class LiveContextState(
    val activeContextId: String? = null,
    val recentFragments: List<String> = emptyList(),
    val momentum: Double = 0.0, // Velocidade/intensidade do fluxo de pensamento
    val topicDrift: Double = 0.0, // O quanto o assunto está desviando do contexto ativo
    val lastMeaningfulInput: String? = null,
    val lastUpdateTimestamp: Long = System.currentTimeMillis()
)
