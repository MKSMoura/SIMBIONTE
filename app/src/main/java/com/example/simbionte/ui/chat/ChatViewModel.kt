package com.example.simbionte.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.simbionte.core.engine.Brain
import com.example.simbionte.core.engine.Event
import com.example.simbionte.core.engine.LiveContextState
import com.example.simbionte.core.engine.State
import com.example.simbionte.core.model.Chat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.random.Random

enum class CognitiveMood { FLOW_HIGH, FLOW_MED, FLOW_LOW, DRIFT, IDLE }

data class CognitiveStateUi(
    val statusText: String = "",
    val mood: CognitiveMood = CognitiveMood.IDLE,
    val isThinking: Boolean = false
)

class ChatViewModel(
    private val brain: Brain,
    private val conversaId: String
) : ViewModel() {

    private val _mensagens = MutableStateFlow<List<Chat>>(emptyList())
    val mensagens: StateFlow<List<Chat>> = _mensagens.asStateFlow()

    private val _cognitiveState = MutableStateFlow(CognitiveStateUi())
    val cognitiveState: StateFlow<CognitiveStateUi> = _cognitiveState.asStateFlow()

    private val thinkingMessages = listOf(
        "Refletindo…", "Processando…", "Deixe-me ver…",
        "Puxando memórias…", "Hmm…", "Conectando ideias…",
        "Pensando…", "Buscando…", "Matutando…", "Aguardando…"
    )

    init {
        observarMemorias()
        observarEventos()
    }

    private var primeiraCarga = true

    private fun observarMemorias() {
        viewModelScope.launch {
            try {
                brain.getMemoryStream(conversaId).collectLatest { memories ->
                    val vazia = memories.isEmpty()
                    if (primeiraCarga && vazia) {
                        primeiraCarga = false
                        brain.sendInitialGreeting(conversaId)
                    }
                    primeiraCarga = false
                    _mensagens.value = memories.map {
                        Chat(
                            id = it.id,
                            conversaId = it.conversaId,
                            texto = it.content,
                            enviada = it.isFromUser,
                            tags = it.tags,
                            relevanceScore = it.relevanceScore
                        )
                    }
                }
            } catch (_: Exception) {
                _mensagens.value = listOf(Chat("error", conversaId, "Erro ao carregar mensagens", false))
            }
        }
    }

    private fun observarEventos() {
        viewModelScope.launch {
            try {
                brain.events.collectLatest { event ->
                    when (event) {
                        is Event.StateChange -> {
                            atualizarEstado(event.state, event.cognitiveState)
                        }
                        is Event.SystemMessage -> {
                            val sysMsg = Chat("sys", conversaId, "Sistema: ${event.kind}", false)
                            _mensagens.value = _mensagens.value + sysMsg
                        }
                        else -> {}
                    }
                }
            } catch (_: Exception) {}
        }
    }

    private fun atualizarEstado(state: State, cognitiveState: LiveContextState?) {
        when (state) {
            State.PENSANDO -> {
                val msg = thinkingMessages[Random.nextInt(thinkingMessages.size)]
                _cognitiveState.value = CognitiveStateUi(
                    statusText = msg,
                    isThinking = true
                )
            }
            State.AQUI -> {
                val novoEstado = if (cognitiveState != null && cognitiveState.recentFragments.isNotEmpty()) {
                    val momentumPct = "%.0f".format(cognitiveState.momentum * 100)
                    val driftPct = "%.0f".format(cognitiveState.topicDrift * 100)
                    val mood = when {
                        cognitiveState.topicDrift > 0.5 -> CognitiveMood.DRIFT
                        cognitiveState.momentum > 0.6 -> CognitiveMood.FLOW_HIGH
                        cognitiveState.momentum > 0.3 -> CognitiveMood.FLOW_MED
                        else -> CognitiveMood.FLOW_LOW
                    }
                    CognitiveStateUi(
                        statusText = "fluxo $momentumPct% | deriva $driftPct%",
                        mood = mood
                    )
                } else {
                    CognitiveStateUi(mood = CognitiveMood.IDLE)
                }
                _cognitiveState.value = novoEstado
            }
        }
    }

    fun enviarMensagem(texto: String) {
        if (texto.isBlank()) return
        brain.onUserInput(conversaId, texto)
    }

    class Factory(
        private val brain: Brain,
        private val conversaId: String
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ChatViewModel(brain, conversaId) as T
        }
    }
}
