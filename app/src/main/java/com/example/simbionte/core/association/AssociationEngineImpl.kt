package com.example.simbionte.core.association

import com.example.simbionte.core.data.MemoryRepository
import com.example.simbionte.core.engine.WordSimilarity
import kotlinx.coroutines.flow.firstOrNull

class AssociationEngineImpl(
    private val wordSimilarity: WordSimilarity = WordSimilarity(),
    private val repository: MemoryRepository
) : AssociationEngine {

    override suspend fun findRelated(userInput: String, limit: Int): List<AssociationCandidate> {
        if (userInput.isBlank()) return emptyList()

        val allMemories = repository.allMemories().firstOrNull() ?: return emptyList()
        if (allMemories.isEmpty()) return emptyList()

        return wordSimilarity.searchSimilar(
            query = userInput,
            corpus = allMemories,
            topK = limit,
            threshold = 0.08
        ).map { (block, sim) ->
            AssociationCandidate(
                memoryId = block.id,
                conversationId = block.conversaId,
                similarity = sim,
                content = block.content
            )
        }
    }
}
