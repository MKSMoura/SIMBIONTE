package com.example.simbionte.core.model

data class Chat(
    val id: String,
    val conversaId: String,
    val texto: String,
    val enviada: Boolean,
    val tags: String? = null,
    val relevanceScore: Float = 0f
)
