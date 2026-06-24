package com.example.simbionte.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ContinuityDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(trace: ContinuityTrace)

    @Query("SELECT * FROM continuity_traces WHERE traceKind = 'THREAD'")
    suspend fun listarTodasThreads(): List<ContinuityTrace>

    @Query("SELECT * FROM continuity_traces WHERE traceKind = 'THREAD' AND closedAt IS NULL")
    suspend fun listarThreadsAbertas(): List<ContinuityTrace>

    @Query("SELECT * FROM continuity_traces WHERE traceKind = 'SESSION' ORDER BY openedAt DESC")
    suspend fun listarSessoes(): List<ContinuityTrace>

    @Query("SELECT * FROM continuity_traces WHERE conversaId = :cid AND traceKind = 'THREAD'")
    suspend fun buscarThread(cid: String): ContinuityTrace?

    @Query("SELECT * FROM continuity_traces WHERE id = :id")
    suspend fun buscarPorId(id: String): ContinuityTrace?
}
