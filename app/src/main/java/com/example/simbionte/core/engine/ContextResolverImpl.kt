package com.example.simbionte.core.engine

import com.example.simbionte.db.MemoryBlock
import java.util.*

class ContextResolverImpl(
    private val wordSimilarity: WordSimilarity = WordSimilarity()
) : ContextResolver {

    private val SIMILARITY_THRESHOLD = 0.15
    private val STRONG_SIMILARITY = 0.35

    override suspend fun resolveContext(
        text: String,
        recentMemories: List<MemoryBlock>,
        activeContextId: String?
    ): String {
        val textTags = wordSimilarity.extractTags(text)

        // 1. Se o tracker já identificou um contexto e o texto tem afinidade, mantém
        if (activeContextId != null && textTags.isNotEmpty()) {
            val relevantMemories = recentMemories.filter { it.contextId == activeContextId }
            if (relevantMemories.isNotEmpty()) {
                val contextTags = relevantMemories
                    .flatMap { wordSimilarity.extractTags(it.content) }
                    .toSet()
                val affinity = wordSimilarity.tagOverlap(textTags, contextTags)
                if (affinity >= SIMILARITY_THRESHOLD) {
                    return activeContextId
                }
            }
        }

        // 2. Busca o contexto mais similar entre as memórias recentes
        val lastMemory = recentMemories.lastOrNull()
        if (lastMemory != null && textTags.isNotEmpty()) {
            val lastTags = wordSimilarity.extractTags(lastMemory.content)
            val similarity = wordSimilarity.tagOverlap(textTags, lastTags)
            val ngramSim = if (textTags.isNotEmpty()) {
                val textNG = wordSimilarity.charNGrams(text)
                val lastNG = wordSimilarity.charNGrams(lastMemory.content)
                wordSimilarity.cosineSimilarity(textNG, lastNG)
            } else 0.0

            if (similarity >= STRONG_SIMILARITY || ngramSim >= STRONG_SIMILARITY) {
                return lastMemory.contextId ?: UUID.randomUUID().toString()
            }
            if (similarity > 0.0 || ngramSim > 0.0) {
                val tenMinutesInMs = 10 * 60 * 1000
                if (System.currentTimeMillis() - lastMemory.timestamp < tenMinutesInMs) {
                    return lastMemory.contextId ?: UUID.randomUUID().toString()
                }
            }
        }

        // 3. Novo container cognitivo
        return UUID.randomUUID().toString()
    }
}
