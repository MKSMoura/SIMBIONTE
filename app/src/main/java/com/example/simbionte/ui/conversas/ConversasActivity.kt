package com.example.simbionte.ui.conversas

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.simbionte.core.di.ServiceLocator
import com.example.simbionte.ui.chat.ChatActivity
import kotlinx.coroutines.launch

class ConversasActivity : AppCompatActivity() {

    private val viewModel: ConversasViewModel by viewModels {
        ConversasViewModel.Factory(ServiceLocator.getConversationRepository(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observarNavegacao()
    }

    private fun observarNavegacao() {
        lifecycleScope.launch {
            viewModel.abrirConversa.collect { (id, nome) ->
                val intent = Intent(this@ConversasActivity, ChatActivity::class.java)
                intent.putExtra("conversa_id", id)
                intent.putExtra("conversa_nome", nome)
                startActivity(intent)
                finish()
            }
        }
    }
}
