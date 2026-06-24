package com.example.simbionte.db

import androidx.room.TypeConverter
import com.example.simbionte.core.engine.RelationType

class Converters {
    @TypeConverter
    fun fromRelationType(value: RelationType): String {
        return value.name
    }

    @TypeConverter
    fun toRelationType(value: String): RelationType {
        return RelationType.valueOf(value)
    }

    @TypeConverter
    fun fromTraceKind(value: TraceKind): String {
        return value.name
    }

    @TypeConverter
    fun toTraceKind(value: String): TraceKind {
        return TraceKind.valueOf(value)
    }
}
