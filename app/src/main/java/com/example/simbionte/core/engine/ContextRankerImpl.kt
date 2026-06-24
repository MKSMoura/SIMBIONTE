package com.example.simbionte.core.engine

import com.example.simbionte.db.MemoryBlock

class ContextRankerImpl : ContextRanker {
    override suspend fun rankTopContexts(allMemories: List<MemoryBlock>): List<String> {
        return allMemories
            .filter { it.contextId != null }
            .groupBy { it.contextId!! }
            .mapValues { entry -> entry.value.sumOf { it.relevanceScore.toDouble() } }
            .toList()
            .sortedByDescending { it.second }
            .take(5)
            .map { it.first }
    }
}
