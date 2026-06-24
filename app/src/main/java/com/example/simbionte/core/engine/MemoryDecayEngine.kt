package com.example.simbionte.core.engine

import com.example.simbionte.db.MemoryBlock

interface MemoryDecayEngine {
    /**
     * Recalcula o cognitiveSignal de um bloco baseado no tempo passado
     * e no histórico de reforço.
     */
    fun calculateDecay(block: MemoryBlock): Float

    /**
     * Retorna o novo cognitiveSignal após um reforço (recorrência).
     */
    fun reinforce(block: MemoryBlock): Float
}
