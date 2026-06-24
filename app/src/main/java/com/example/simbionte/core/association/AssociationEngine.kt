package com.example.simbionte.core.association

interface AssociationEngine {
    suspend fun findRelated(userInput: String, limit: Int = 5): List<AssociationCandidate>
}
