package com.example.simbionte.core.engine

import com.example.simbionte.db.MemoryBlock

enum class TemporalWindow {
    IMMEDIATE, // Poucos segundos/minutos
    RECENT,    // Horas
    DISTANT    // Dias ou mais
}

interface TimelineEngine {
    /**
     * Analisa a distância temporal entre duas memórias para definir a janela cognitiva.
     */
    fun identifyWindow(newTimestamp: Long, lastTimestamp: Long?): TemporalWindow
}
