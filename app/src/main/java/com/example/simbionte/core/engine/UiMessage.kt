package com.example.simbionte.core.engine

data class UiMessage(
    val text: String,
    val fromUser: Boolean,
    val isSystem: Boolean = false
)
