package com.example.simbionte.core.data

import com.example.simbionte.core.engine.RelationType
import com.example.simbionte.db.ContextWindow
import com.example.simbionte.db.MemoryBlock
import com.example.simbionte.db.MemoryDao
import com.example.simbionte.db.MemoryRelation
import kotlinx.coroutines.flow.Flow
import java.util.*

class MemoryRepository(private val memoryDao: MemoryDao) {

    fun allMemories(): Flow<List<MemoryBlock>> = memoryDao.listarTodos()

    fun getMemoriesByConversation(conversaId: String): Flow<List<MemoryBlock>> = 
        memoryDao.listarPorConversa(conversaId)

    suspend fun saveMemory(
        conversaId: String, 
        content: String, 
        isFromUser: Boolean, 
        tags: String? = null,
        contextId: String? = null,
        relevanceScore: Float = 0f,
        cognitiveSignal: Float = 1.0f
    ): MemoryBlock {
        val block = MemoryBlock(
            id = UUID.randomUUID().toString(),
            conversaId = conversaId,
            contextId = contextId,
            content = content,
            isFromUser = isFromUser,
            timestamp = System.currentTimeMillis(),
            tags = tags,
            relevanceScore = relevanceScore,
            cognitiveSignal = cognitiveSignal,
            lastReinforcedAt = System.currentTimeMillis()
        )
        memoryDao.inserir(block)
        return block
    }

    suspend fun updateMemory(block: MemoryBlock) {
        memoryDao.inserir(block)
    }

    suspend fun searchMemories(query: String): List<MemoryBlock> {
        return memoryDao.buscarPorTexto(query)
    }

    suspend fun relate(fromId: String, toId: String, weight: Float, type: RelationType) {
        memoryDao.inserirRelacao(MemoryRelation(fromId, toId, weight, type))
    }

    suspend fun saveContext(context: ContextWindow) {
        memoryDao.inserirContexto(context)
    }

    suspend fun getContext(id: String): ContextWindow? {
        return memoryDao.buscarContexto(id)
    }

    suspend fun getTopContexts(): List<ContextWindow> {
        return memoryDao.listarContextosPorRelevancia()
    }

    suspend fun getAllRelations(): List<MemoryRelation> {
        return memoryDao.listarTodasRelacoes()
    }

    suspend fun getRelationsForBlock(blockId: String): List<MemoryRelation> {
        return memoryDao.listarRelacoesDoBloco(blockId)
    }

    suspend fun getStaleContexts(threshold: Float = 0.1f): List<ContextWindow> {
        return memoryDao.listarContextosEstagnados(threshold)
    }

    suspend fun deleteContext(contextId: String) {
        memoryDao.deletarContexto(contextId)
    }

    suspend fun updateCognitiveSignal(id: String, signal: Float) {
        memoryDao.atualizarSinalCognitivo(id, signal)
    }

    suspend fun getAllContextIds(): List<String> {
        return memoryDao.listarContextIds()
    }

    suspend fun deleteRelation(relation: MemoryRelation) {
        memoryDao.deletarRelacao(relation)
    }

    suspend fun getAllBlockIds(): List<String> {
        return memoryDao.listarTodosIds()
    }
}
