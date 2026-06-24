package com.example.simbionte.core.data

import com.example.simbionte.core.model.Chat
import com.example.simbionte.core.model.Conversas
import com.example.simbionte.db.MemoryBlock
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class ConversationRepository(private val memoryRepository: MemoryRepository) {

    suspend fun listarConversas(): List<Conversas> {
        val allMemories = memoryRepository.allMemories().firstOrNull() ?: emptyList()
        return allMemories
            .groupBy { it.conversaId }
            .map { (conversaId, memoryBlocks) ->
                val ultimaMensagem = memoryBlocks
                    .maxByOrNull { it.timestamp }
                    ?.content ?: ""
                val hora = formatarHora(memoryBlocks.maxByOrNull { it.timestamp }?.timestamp)
                val naoLidas = memoryBlocks.count { !it.isFromUser }
                
                Conversas(
                    id = conversaId,
                    nome = obterNomeConversa(conversaId, memoryBlocks),
                    ultimaMensagem = ultimaMensagem.take(50),
                    hora = hora,
                    naoLidas = naoLidas
                )
            }
            .sortedByDescending { it.hora }
    }

    suspend fun getOrCreateSingleConversationId(): String {
        val conversas = listarConversas()
        if (conversas.isNotEmpty()) return conversas.first().id
        return UUID.randomUUID().toString()
    }

    suspend fun buscarConversaPorId(id: String): Conversas? {
        return listarConversas().find { it.id == id }
    }

    suspend fun listarMensagens(conversaId: String): List<Chat> {
        val memories = memoryRepository.getMemoriesByConversation(conversaId).firstOrNull() ?: emptyList()
        return memories
            .map { memory ->
                Chat(
                    id = memory.id,
                    conversaId = memory.conversaId,
                    texto = memory.content,
                    enviada = memory.isFromUser,
                    tags = memory.tags,
                    relevanceScore = memory.relevanceScore
                )
            }
    }

    suspend fun enviarMensagem(conversaId: String, texto: String, enviada: Boolean) {
        memoryRepository.saveMemory(
            conversaId = conversaId,
            content = texto,
            isFromUser = enviada
        )
    }

    suspend fun buscarMensagemPorId(id: String): Chat? {
        val memories = memoryRepository.searchMemories(id)
        return memories.find { it.id == id }?.let { memory ->
            Chat(
                id = memory.id,
                conversaId = memory.conversaId,
                texto = memory.content,
                enviada = memory.isFromUser,
                tags = memory.tags,
                relevanceScore = memory.relevanceScore
            )
        }
    }

    suspend fun indexarMensagens() {
        val allMemories = memoryRepository.allMemories().firstOrNull() ?: emptyList()
        allMemories.forEach { memory ->
            val tags = extrairTags(memory.content)
            val memoryAtualizada = memory.copy(tags = tags)
            memoryRepository.updateMemory(memoryAtualizada)
        }
    }

    suspend fun pesquisarMensagens(query: String): List<Chat> {
        val memories = memoryRepository.searchMemories(query)
        return memories.map { memory ->
            Chat(
                id = memory.id,
                conversaId = memory.conversaId,
                texto = memory.content,
                enviada = memory.isFromUser,
                tags = memory.tags,
                relevanceScore = memory.relevanceScore
            )
        }
    }

    suspend fun sincronizar() {
        indexarMensagens()
    }

    private fun obterNomeConversa(conversaId: String, memories: List<MemoryBlock>): String {
        memories.firstOrNull { it.isFromUser }?.content?.take(25)?.let { return it }
        memories.firstOrNull()?.content?.take(25)?.let { return it }
        return "Conversa"
    }

    private fun formatarHora(timestamp: Long?): String {
        if (timestamp == null) return ""
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        
        return when {
            diff < 60 * 60 * 1000 -> "Agora"
            diff < 24 * 60 * 60 * 1000 -> "Hoje"
            diff < 48 * 60 * 60 * 1000 -> "Ontem"
            else -> java.text.SimpleDateFormat("dd/MM", java.util.Locale.getDefault())
                .format(java.util.Date(timestamp))
        }
    }

    private fun extrairTags(text: String): String {
        return text.split(" ")
            .map { it.lowercase().replace(Regex("""\P{L}"""), "") }
            .filter { it.length >= 4 }
            .distinct()
            .joinToString(",")
    }
}
