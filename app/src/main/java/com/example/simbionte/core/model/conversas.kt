package com.example.simbionte.core.model

data class Conversas(
    val id: String,
    val nome: String,
    val ultimaMensagem: String = "",
    val hora: String = "",
    val naoLidas: Int = 0
)