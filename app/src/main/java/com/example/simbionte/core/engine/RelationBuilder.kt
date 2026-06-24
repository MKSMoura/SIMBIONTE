package com.example.simbionte.core.engine

import com.example.simbionte.db.MemoryBlock
import com.example.simbionte.db.MemoryRelation

interface RelationBuilder {
    /**
     * Gera a malha de relações para um novo bloco de memória,
     * considerando o contexto temporal, similaridade e dominância.
     */
    fun buildRelations(
        newBlock: MemoryBlock,
        lastBlock: MemoryBlock?,
        window: TemporalWindow,
        similarMemories: List<MemoryBlock>,
        dominantContexts: List<String>
    ): List<MemoryRelation>
}
