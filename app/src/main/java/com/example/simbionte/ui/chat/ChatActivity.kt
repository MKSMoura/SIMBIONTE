package com.example.simbionte.ui.chat

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.simbionte.R
import com.example.simbionte.core.engine.Brain
import com.example.simbionte.databinding.ActivityChatBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private lateinit var adapter: ChatAdapter
    private var conversaId: String = "default"
    private var conversaNome: String = "Simbionte"
    private val brain = Brain.getInstance()

    private val viewModel: ChatViewModel by viewModels {
        ChatViewModel.Factory(brain, conversaId)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        conversaId = intent.getStringExtra("conversa_id") ?: "default"
        conversaNome = intent.getStringExtra("conversa_nome") ?: "Simbionte"

        setupViews()
        setupObservers()
    }

    private fun setupViews() {
        binding.textTituloConversa.text = conversaNome
        adapter = ChatAdapter(mutableListOf())
        binding.recyclerViewChat.layoutManager = LinearLayoutManager(this).apply {
            stackFromEnd = true
        }
        binding.recyclerViewChat.adapter = adapter

        binding.buttonEnviar.setOnClickListener {
            val texto = binding.editMensagem.text.toString().trim()
            if (texto.isNotEmpty()) {
                viewModel.enviarMensagem(texto)
                binding.editMensagem.text.clear()
            }
        }

        binding.toolbarChat.setNavigationOnClickListener {
            finish()
        }

        binding.imageAvatarToolbar.setImageResource(android.R.drawable.ic_menu_gallery)
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            viewModel.mensagens.collectLatest { mensagens ->
                adapter.atualizarDados(mensagens)
                if (mensagens.isNotEmpty()) {
                    binding.recyclerViewChat.scrollToPosition(mensagens.size - 1)
                }
                val charName = brain.getCharacterName()
                if (!charName.isNullOrBlank() && charName != conversaNome) {
                    conversaNome = charName
                    binding.textTituloConversa.text = conversaNome
                }
            }
        }

        lifecycleScope.launch {
            viewModel.cognitiveState.collectLatest { state ->
                when {
                    state.isThinking -> {
                        binding.textStatus.text = getString(R.string.status_typing)
                        binding.textStatus.setTextColor(getColor(R.color.simbionte_primary))
                    }
                    state.mood == CognitiveMood.IDLE -> {
                        binding.textStatus.setText(R.string.status_seen)
                        binding.textStatus.setTextColor(getColor(R.color.chat_text_secondary))
                    }
                    else -> {
                        binding.textStatus.text = state.statusText
                        val cor = when (state.mood) {
                            CognitiveMood.DRIFT -> R.color.cog_drift
                            CognitiveMood.FLOW_HIGH -> R.color.cog_momentum_alto
                            CognitiveMood.FLOW_MED -> R.color.cog_momentum_medio
                            CognitiveMood.FLOW_LOW -> R.color.cog_momentum_baixo
                            CognitiveMood.IDLE -> R.color.chat_text_secondary
                        }
                        binding.textStatus.setTextColor(ContextCompat.getColor(this@ChatActivity, cor))
                    }
                }
            }
        }
    }
}
