package com.example.simbionte.core.engine

import com.example.simbionte.db.MemoryBlock
import com.example.simbionte.db.MemoryRelation

class RelationBuilderImpl(
    private val wordSimilarity: WordSimilarity = WordSimilarity()
) : RelationBuilder {

    override fun buildRelations(
        newBlock: MemoryBlock,
        lastBlock: MemoryBlock?,
        window: TemporalWindow,
        similarMemories: List<MemoryBlock>,
        dominantContexts: List<String>
    ): List<MemoryRelation> {
        val relations = mutableListOf<MemoryRelation>()
        val newTags = wordSimilarity.extractTags(newBlock.content)

        // 1. Relação temporal com o bloco anterior
        lastBlock?.let { last ->
            val lastTags = wordSimilarity.extractTags(last.content)
            val tagSim = wordSimilarity.tagOverlap(newTags, lastTags)

            val type = when {
                tagSim > 0.3 -> RelationType.CONTINUATION
                window == TemporalWindow.IMMEDIATE -> RelationType.CONTINUATION
                window == TemporalWindow.RECENT -> RelationType.SIDE_THOUGHT
                else -> RelationType.ASSOCIATION
            }
            val weight = (1.0f * tagSim.toFloat()).coerceAtLeast(0.3f).coerceAtMost(1.0f)
            relations.add(MemoryRelation(newBlock.id, last.id, weight, type))
        }

        // 2. CONTRADICTION por similaridade de conteúdo (n-gram) + palavras negativas
        lastBlock?.let { last ->
            val ngramSim = wordSimilarity.charNGrams(newBlock.content).let { newNG ->
                wordSimilarity.charNGrams(last.content).let { lastNG ->
                    wordSimilarity.cosineSimilarity(newNG, lastNG)
                }
            }
            val levSim = wordSimilarity.levenshteinSimilarity(newBlock.content, last.content)
            val negativeTerms = listOf("não", "mas", "errado", "mentira", "engano", "inverso",
                "contradição", "oposto", "discordo", "inverte", "inverter")
            val hasNegation = negativeTerms.any { newBlock.content.lowercase().contains(it) }

            if (hasNegation && ngramSim > 0.15 && ngramSim < 0.6) {
                relations.add(MemoryRelation(newBlock.id, last.id, 0.85f, RelationType.CONTRADICTION))
            } else if (levSim > 0.7 && hasNegation) {
                relations.add(MemoryRelation(newBlock.id, last.id, 0.9f, RelationType.CONTRADICTION))
            }
        }

        // 3. RETURN e ASSOCIATION por similaridade semântica + dominância
        similarMemories.forEach { similar ->
            if (similar.id == lastBlock?.id) return@forEach
            val isFromDominantContext = dominantContexts.contains(similar.contextId)
            val isOldMemory = (newBlock.timestamp - similar.timestamp) > (60 * 60 * 1000)

            val newNGrams = wordSimilarity.charNGrams(newBlock.content)
            val simNGrams = wordSimilarity.charNGrams(similar.content)
            val ngramSim = wordSimilarity.cosineSimilarity(newNGrams, simNGrams)

            val newS = wordSimilarity.extractTags(newBlock.content)
            val simS = wordSimilarity.extractTags(similar.content)
            val tagSim = wordSimilarity.tagOverlap(newS, simS)

            if (isFromDominantContext && isOldMemory && (ngramSim > 0.15 || tagSim > 0.2)) {
                relations.add(MemoryRelation(newBlock.id, similar.id, 0.85f, RelationType.RETURN))
            } else if (tagSim > 0.15 || ngramSim > 0.2) {
                val weight = (0.4f + ngramSim.toFloat() * 0.3f + tagSim.toFloat() * 0.3f).coerceAtMost(1.0f)
                relations.add(MemoryRelation(newBlock.id, similar.id, weight, RelationType.ASSOCIATION))
            } else if (isFromDominantContext) {
                relations.add(MemoryRelation(newBlock.id, similar.id, 0.3f, RelationType.ASSOCIATION))
            }
        }

        // 4. ELABORATION: quando o novo bloco expande o anterior com alta sobreposição de tags
        lastBlock?.let { last ->
            val lastTags = wordSimilarity.extractTags(last.content)
            val tagSim = wordSimilarity.tagOverlap(newTags, lastTags)
            if (tagSim > 0.25 && newBlock.content.length > last.content.length * 1.3) {
                relations.add(MemoryRelation(newBlock.id, last.id, 0.75f, RelationType.CONTINUATION))
            }
        }

        return relations
    }
}
