package com.example.simbionte.core.engine

import com.example.simbionte.core.data.MemoryRepository
import kotlinx.coroutines.flow.firstOrNull

data class ConsolidationSummary(
    val memoriesDecayed: Int,
    val contextsPruned: Int,
    val relationsClean: Int,
    val timestamp: Long
)

class ConsolidationEngine(
    private val repository: MemoryRepository,
    private val decayEngine: MemoryDecayEngine = MemoryDecayEngineImpl(),
    private val wordSimilarity: WordSimilarity = WordSimilarity(),
    private val centralityEngine: CentralityEngine = CentralityEngine()
) {

    private var memoriesDecayed = 0
    private var contextsPruned = 0
    private var relationsClean = 0
    private var lastSummary = ConsolidationSummary(0, 0, 0, 0L)

    suspend fun consolidate() {
        memoriesDecayed = 0
        contextsPruned = 0
        relationsClean = 0
        applyDecayToAll()
        pruneStaleContexts()
        mergeSimilarContexts()
        cleanupOrphanedRelations()
        lastSummary = ConsolidationSummary(memoriesDecayed, contextsPruned, relationsClean, System.currentTimeMillis())
    }

    fun getSummary(): ConsolidationSummary = lastSummary

    private suspend fun applyDecayToAll() {
        val allMemories = repository.allMemories().firstOrNull() ?: emptyList()
        val allRelations = repository.getAllRelations()
        val centralityMap = centralityEngine.calculateAll(allRelations)

        allMemories.forEach { block ->
            val centrality = centralityMap[block.id]
            val centralBoost = centrality?.let { centralityEngine.getCentralityBoost(it) } ?: 0f

            val decayed = decayEngine.calculateDecay(block)
            val boostedSignal = (decayed + centralBoost).coerceAtMost(5.0f)

            if (boostedSignal != block.cognitiveSignal) {
                repository.updateCognitiveSignal(block.id, boostedSignal)
                memoriesDecayed++
            }
        }
    }

    private suspend fun pruneStaleContexts() {
        val staleContexts = repository.getStaleContexts(threshold = 0.15f)
        staleContexts.forEach { context ->
            repository.deleteContext(context.contextId)
            contextsPruned++
        }
    }

    private suspend fun mergeSimilarContexts() {
        val allContextIds = repository.getAllContextIds()
        if (allContextIds.size < 2) return

        val contextTags = allContextIds.associateWith { ctxId ->
            val context = repository.getContext(ctxId)
            context?.dominantTags?.split(",")?.filter { it.isNotBlank() }?.toSet() ?: emptySet()
        }

        val merged = mutableSetOf<String>()

        for (i in allContextIds.indices) {
            if (allContextIds[i] in merged) continue
            for (j in i + 1 until allContextIds.size) {
                if (allContextIds[j] in merged) continue
                val tagsA = contextTags[allContextIds[i]] ?: continue
                val tagsB = contextTags[allContextIds[j]] ?: continue
                if (tagsA.isEmpty() || tagsB.isEmpty()) continue

                val overlap = wordSimilarity.tagOverlap(tagsA, tagsB)
                if (overlap > 0.5) {
                    repository.deleteContext(allContextIds[j])
                    merged.add(allContextIds[j])
                    contextsPruned++
                }
            }
        }
    }

    private suspend fun cleanupOrphanedRelations() {
        val allRelations = repository.getAllRelations()
        val validIds = repository.getAllBlockIds().toSet()
        val orphanedRelations = allRelations.filter { relation ->
            relation.fromId !in validIds || relation.toId !in validIds
        }
        orphanedRelations.forEach {
            repository.deleteRelation(it)
            relationsClean++
        }
    }
}
