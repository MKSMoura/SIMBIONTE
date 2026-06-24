package com.example.simbionte.core.engine

import com.example.simbionte.db.MemoryBlock

interface CognitivePipeline {
    /**
     * Executa o ciclo cognitivo completo para uma nova entrada do usuário.
     * Retorna o bloco de memória criado e a lista de relações geradas.
     */
    suspend fun process(conversaId: String, text: String): PipelineResult
}

data class PipelineResult(
    val currentBlock: MemoryBlock,
    val relations: List<com.example.simbionte.db.MemoryRelation>,
    val contextId: String,
    val window: TemporalWindow
)
