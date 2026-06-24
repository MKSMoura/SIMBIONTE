package com.example.simbionte.core.continuity

import com.example.simbionte.db.ContinuityDao
import com.example.simbionte.db.ContinuityTrace
import com.example.simbionte.db.TraceKind
import java.util.UUID

class ContinuityEngine(private val dao: ContinuityDao) {

    @Volatile
    private var sessionId: String? = null

    suspend fun startSession() {
        if (sessionId != null) return
        val now = System.currentTimeMillis()
        val id = UUID.randomUUID().toString()
        val trace = ContinuityTrace(
            id = id,
            conversaId = null,
            traceKind = TraceKind.SESSION,
            openedAt = now,
            lastTouchedAt = now,
            closedAt = null
        )
        dao.inserir(trace)
        sessionId = id
    }

    suspend fun endSession() {
        val id = sessionId ?: return
        val now = System.currentTimeMillis()
        val existing = dao.buscarPorId(id) ?: return
        dao.inserir(existing.copy(closedAt = now))
        sessionId = null
    }

    suspend fun openThread(conversaId: String) {
        val existing = dao.buscarThread(conversaId)
        val now = System.currentTimeMillis()
        if (existing == null) {
            val trace = ContinuityTrace(
                id = UUID.randomUUID().toString(),
                conversaId = conversaId,
                traceKind = TraceKind.THREAD,
                openedAt = now,
                lastTouchedAt = now,
                closedAt = null
            )
            dao.inserir(trace)
        } else if (existing.closedAt == null) {
            dao.inserir(existing.copy(lastTouchedAt = now))
        }
    }

    suspend fun touchThread(conversaId: String) {
        val existing = dao.buscarThread(conversaId) ?: return
        if (existing.closedAt != null) return
        dao.inserir(existing.copy(lastTouchedAt = System.currentTimeMillis()))
    }

    suspend fun closeThread(conversaId: String) {
        val existing = dao.buscarThread(conversaId) ?: return
        if (existing.closedAt != null) return
        dao.inserir(existing.copy(closedAt = System.currentTimeMillis()))
    }

    suspend fun getState(): ContinuityState {
        val threadsAbertas = dao.listarThreadsAbertas()
        val todasThreads = dao.listarTodasThreads()
        val ultimoToque = threadsAbertas.maxOfOrNull { it.lastTouchedAt }
        return ContinuityState(
            emSessao = sessionId != null,
            threadsAbertas = threadsAbertas.mapNotNull { it.conversaId },
            threadsTotal = todasThreads.size,
            ultimaInteracao = ultimoToque
        )
    }
}
