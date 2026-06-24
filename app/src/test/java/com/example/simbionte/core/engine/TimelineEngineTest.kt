package com.example.simbionte.core.engine

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TimelineEngineTest {

    private lateinit var engine: TimelineEngineImpl

    @Before
    fun setup() {
        engine = TimelineEngineImpl()
    }

    @Test
    fun `null lastTimestamp returns DISTANT`() {
        val window = engine.identifyWindow(System.currentTimeMillis(), null)
        assertEquals(TemporalWindow.DISTANT, window)
    }

    @Test
    fun `difference under 5 minutes returns IMMEDIATE`() {
        val now = System.currentTimeMillis()
        val window = engine.identifyWindow(now, now - 60_000) // 1 minuto
        assertEquals(TemporalWindow.IMMEDIATE, window)
    }

    @Test
    fun `difference under 24 hours returns RECENT`() {
        val now = System.currentTimeMillis()
        val window = engine.identifyWindow(now, now - 3_600_000) // 1 hora
        assertEquals(TemporalWindow.RECENT, window)
    }

    @Test
    fun `difference over 24 hours returns DISTANT`() {
        val now = System.currentTimeMillis()
        val window = engine.identifyWindow(now, now - 86_400_000) // 24 horas
        assertEquals(TemporalWindow.DISTANT, window)
    }
}
