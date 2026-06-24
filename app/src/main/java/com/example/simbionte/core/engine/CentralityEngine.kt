package com.example.simbionte.core.engine

import com.example.simbionte.db.MemoryRelation

class CentralityEngine {

    data class CentralityResult(
        val blockId: String,
        val degree: Int,
        val weightedDegree: Double,
        val relationTypes: Map<RelationType, Int>
    )

    fun calculateAll(allRelations: List<MemoryRelation>): Map<String, CentralityResult> {
        val degreeMap = mutableMapOf<String, MutableList<MemoryRelation>>()

        allRelations.forEach { rel ->
            degreeMap.getOrPut(rel.fromId) { mutableListOf() }.add(rel)
            degreeMap.getOrPut(rel.toId) { mutableListOf() }.add(rel)
        }

        return degreeMap.mapValues { (blockId, rels) ->
            CentralityResult(
                blockId = blockId,
                degree = rels.size,
                weightedDegree = rels.sumOf { it.weight.toDouble() },
                relationTypes = rels.groupBy { it.relationshipType }.mapValues { it.value.size }
            )
        }
    }

    fun getCentralityBoost(centrality: CentralityResult): Float {
        var boost = 0.0f
        if (centrality.degree >= 5) boost += 0.5f
        else if (centrality.degree >= 3) boost += 0.3f
        else if (centrality.degree >= 1) boost += 0.1f

        if (centrality.weightedDegree > 3.0) boost += 0.3f
        else if (centrality.weightedDegree > 1.5) boost += 0.15f

        val returnCount = centrality.relationTypes[RelationType.RETURN] ?: 0
        if (returnCount >= 2) boost += 0.2f

        return boost.coerceAtMost(1.5f)
    }
}
