package com.example.simbionte.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "context_windows")
data class ContextWindow(
    @PrimaryKey val contextId: String,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val dominantTags: String, // CSV de tags que definem este contexto
    val memoryCount: Int,
    val relevanceScore: Float,
    val cognitiveSignal: Float = 1.0f
)
