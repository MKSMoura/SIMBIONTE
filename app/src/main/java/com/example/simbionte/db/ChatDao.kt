package com.example.simbionte.db

import androidx.room.*
import com.example.simbionte.core.engine.RelationType
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "memory_blocks")
data class MemoryBlock(
    @PrimaryKey val id: String,
    val conversaId: String,
    val contextId: String?,
    val content: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val tags: String? = null,
    val relevanceScore: Float = 0f,
    val cognitiveSignal: Float = 1.0f,
    val lastReinforcedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "memory_relations",
    primaryKeys = ["fromId", "toId"]
)
data class MemoryRelation(
    val fromId: String,
    val toId: String,
    val weight: Float,
    val relationshipType: RelationType
)

@Dao
interface MemoryDao {
    @Query("SELECT * FROM memory_blocks WHERE conversaId = :cid ORDER BY timestamp ASC")
    fun listarPorConversa(cid: String): Flow<List<MemoryBlock>>

    @Query("SELECT * FROM memory_blocks ORDER BY timestamp ASC")
    fun listarTodos(): Flow<List<MemoryBlock>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserir(block: MemoryBlock)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirRelacao(relation: MemoryRelation)

    @Query("SELECT * FROM memory_blocks WHERE content LIKE '%' || :query || '%'")
    suspend fun buscarPorTexto(query: String): List<MemoryBlock>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun inserirContexto(context: ContextWindow)

    @Query("SELECT * FROM context_windows WHERE contextId = :id")
    suspend fun buscarContexto(id: String): ContextWindow?

    @Query("SELECT * FROM context_windows ORDER BY relevanceScore DESC")
    suspend fun listarContextosPorRelevancia(): List<ContextWindow>

    @Query("SELECT * FROM memory_relations")
    suspend fun listarTodasRelacoes(): List<MemoryRelation>

    @Query("SELECT * FROM memory_relations WHERE fromId = :blockId OR toId = :blockId")
    suspend fun listarRelacoesDoBloco(blockId: String): List<MemoryRelation>

    @Query("SELECT * FROM context_windows WHERE relevanceScore < :threshold")
    suspend fun listarContextosEstagnados(threshold: Float): List<ContextWindow>

    @Query("DELETE FROM context_windows WHERE contextId = :contextId")
    suspend fun deletarContexto(contextId: String)

    @Query("UPDATE memory_blocks SET cognitiveSignal = :signal, lastReinforcedAt = :now WHERE id = :id")
    suspend fun atualizarSinalCognitivo(id: String, signal: Float, now: Long = System.currentTimeMillis())

    @Delete
    suspend fun deletarMemoria(block: MemoryBlock)

    @Query("DELETE FROM memory_relations")
    suspend fun limparRelacoes()

    @Query("DELETE FROM memory_relations WHERE fromId NOT IN (SELECT id FROM memory_blocks) OR toId NOT IN (SELECT id FROM memory_blocks)")
    suspend fun limparRelacoesOrfas()

    @Delete
    suspend fun deletarRelacao(relation: MemoryRelation)

    @Delete
    suspend fun deletarBlocos(blocks: List<MemoryBlock>)

    @Query("SELECT DISTINCT contextId FROM memory_blocks WHERE contextId IS NOT NULL")
    suspend fun listarContextIds(): List<String>

    @Query("SELECT id FROM memory_blocks")
    suspend fun listarTodosIds(): List<String>
}
