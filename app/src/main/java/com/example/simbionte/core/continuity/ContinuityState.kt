package com.example.simbionte.core.continuity

data class ContinuityState(
    val emSessao: Boolean,
    val threadsAbertas: List<String>,
    val threadsTotal: Int,
    val ultimaInteracao: Long?
)
