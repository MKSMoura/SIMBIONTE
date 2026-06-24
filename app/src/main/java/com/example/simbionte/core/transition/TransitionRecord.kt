package com.example.simbionte.core.transition

data class TransitionRecord(
    val timestamp: Long,
    val conversaId: String?,
    val fromContextId: String?,
    val toContextId: String?,
    val previousMomentum: Double,
    val currentMomentum: Double,
    val previousTopicDrift: Double,
    val currentTopicDrift: Double
)
