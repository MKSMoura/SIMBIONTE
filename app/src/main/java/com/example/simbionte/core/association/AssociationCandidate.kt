package com.example.simbionte.core.association

data class AssociationCandidate(
    val memoryId: String,
    val conversationId: String,
    val similarity: Double,
    val content: String
)
