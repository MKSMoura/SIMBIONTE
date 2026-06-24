package com.example.simbionte.db

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class TraceKind {
    THREAD,
    SESSION
}

@Entity(tableName = "continuity_traces")
data class ContinuityTrace(
    @PrimaryKey val id: String,
    val conversaId: String?,
    val traceKind: TraceKind,
    val openedAt: Long,
    val lastTouchedAt: Long,
    val closedAt: Long?
)
