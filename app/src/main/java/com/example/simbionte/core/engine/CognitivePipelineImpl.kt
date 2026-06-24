package com.example.simbionte.core.engine

import com.example.simbionte.core.data.MemoryRepository
import com.example.simbionte.db.ContextWindow
import com.example.simbionte.db.MemoryBlock
import kotlinx.coroutines.flow.firstOrNull

class CognitivePipelineImpl(
    private val repository: MemoryRepository,
    private val timelineEngine: TimelineEngine,
    private val contextTracker: ContextMemoryTracker,
    private val contextResolver: ContextResolver,
    private val contextRanker: ContextRanker,
    private val relationBuilder: RelationBuilder,
    private val scoringEngine: MemoryScoringEngine,
    private val decayEngine: MemoryDecayEngine,
    private val wordSimilarity: WordSimilarity = WordSimilarity(),
    private val centralityEngine: CentralityEngine = CentralityEngine()
) : CognitivePipeline {

    override suspend fun process(conversaId: String, text: String): PipelineResult {
        val allMemories = repository.allMemories().firstOrNull() ?: emptyList()
        val recentMemories = repository.getMemoriesByConversation(conversaId).firstOrNull() ?: emptyList()
        val allContexts = repository.getTopContexts()
        val lastBlock = recentMemories.lastOrNull()

        // 1. TimelineEngine
        val window = timelineEngine.identifyWindow(System.currentTimeMillis(), lastBlock?.timestamp)

        // 2. ContextMemoryTracker
        contextTracker.track(recentMemories, allContexts)
        val activeContextId = contextTracker.getActiveContextId()

        // 3. ContextResolver semântico
        val contextId = contextResolver.resolveContext(text, recentMemories.takeLast(10), activeContextId)

        // 4. ContextRanker
        val topContexts = contextRanker.rankTopContexts(allMemories)

        // 5. Extração de tags e busca semântica por n-gramas
        val tags = extractTags(text)
        val similarMemories = wordSimilarity.searchSimilar(
            query = text,
            corpus = allMemories,
            topK = 15,
            threshold = 0.08
        ).map { it.first }

        // 6. Reforço cognitivo em memórias similares
        similarMemories.forEach { oldBlock ->
            val reinforcedSignal = decayEngine.reinforce(oldBlock)
            repository.updateMemory(oldBlock.copy(
                cognitiveSignal = reinforcedSignal,
                lastReinforcedAt = System.currentTimeMillis()
            ))
        }

        // 7. Criar bloco de memória
        val currentBlock = repository.saveMemory(
            conversaId = conversaId,
            content = text,
            isFromUser = true,
            tags = tags,
            contextId = contextId,
            cognitiveSignal = 1.0f
        )

        // 8. RelationBuilder com NLP leve
        val relations = relationBuilder.buildRelations(
            newBlock = currentBlock,
            lastBlock = lastBlock,
            window = window,
            similarMemories = similarMemories,
            dominantContexts = topContexts
        )

        relations.forEach { repository.relate(it.fromId, it.toId, it.weight, it.relationshipType) }

        // 9. Centralidade — calcular e aplicar bônus
        val allRelations = repository.getAllRelations()
        val centralityMap = centralityEngine.calculateAll(allRelations)
        val finalBlock = currentBlock.let { block ->
            centralityMap[block.id]?.let { centrality ->
                val boost = centralityEngine.getCentralityBoost(centrality)
                if (boost > 0f) {
                    val newSignal = (block.cognitiveSignal + boost).coerceAtMost(5.0f)
                    repository.updateCognitiveSignal(block.id, newSignal)
                    block.copy(cognitiveSignal = newSignal)
                } else block
            } ?: block
        }

        // 10. Scoring com todos os fatores
        val finalScore = scoringEngine.calculateScore(finalBlock, relations)
        repository.updateMemory(finalBlock.copy(relevanceScore = finalScore))

        // 11. Atualização do ContextWindow
        updateContextWindow(contextId, tags, finalScore)

        return PipelineResult(
            finalBlock.copy(relevanceScore = finalScore),
            relations,
            contextId,
            window
        )
    }

    private fun extractTags(text: String): String {
        return text.split(" ")
            .map { it.lowercase().replace(Regex("""\P{L}"""), "") }
            .filter { it.length >= 4 }
            .distinct()
            .joinToString(",")
    }

    private suspend fun updateContextWindow(contextId: String, tags: String, score: Float) {
        val existingContext = repository.getContext(contextId)
        if (existingContext == null) {
            repository.saveContext(ContextWindow(
                contextId = contextId,
                dominantTags = tags,
                memoryCount = 1,
                relevanceScore = score,
                cognitiveSignal = 1.0f
            ))
        } else {
            repository.saveContext(existingContext.copy(
                updatedAt = System.currentTimeMillis(),
                memoryCount = existingContext.memoryCount + 1,
                relevanceScore = existingContext.relevanceScore + score,
                cognitiveSignal = (existingContext.cognitiveSignal + 0.2f).coerceAtMost(5.0f)
            ))
        }
    }
}
