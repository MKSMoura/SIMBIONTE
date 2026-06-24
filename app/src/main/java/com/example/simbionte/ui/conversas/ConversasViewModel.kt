package com.example.simbionte.ui.conversas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.simbionte.core.data.ConversationRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class ConversasViewModel(
    private val conversationRepository: ConversationRepository
) : ViewModel() {

    private val _abrirConversa = MutableSharedFlow<Pair<String, String>>()
    val abrirConversa: SharedFlow<Pair<String, String>> = _abrirConversa.asSharedFlow()

    init {
        resolverConversaUnica()
    }

    private fun resolverConversaUnica() {
        viewModelScope.launch {
            val id = conversationRepository.getOrCreateSingleConversationId()
            _abrirConversa.emit(id to "Simbionte")
        }
    }

    class Factory(
        private val conversationRepository: ConversationRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ConversasViewModel(conversationRepository) as T
        }
    }
}
