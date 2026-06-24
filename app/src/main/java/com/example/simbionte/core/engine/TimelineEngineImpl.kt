package com.example.simbionte.core.engine

class TimelineEngineImpl : TimelineEngine {
    override fun identifyWindow(newTimestamp: Long, lastTimestamp: Long?): TemporalWindow {
        if (lastTimestamp == null) return TemporalWindow.DISTANT
        
        val diff = newTimestamp - lastTimestamp
        val oneMinute = 60 * 1000
        val oneHour = 60 * oneMinute
        
        return when {
            diff < (5 * oneMinute) -> TemporalWindow.IMMEDIATE
            diff < (24 * oneHour) -> TemporalWindow.RECENT
            else -> TemporalWindow.DISTANT
        }
    }
}
