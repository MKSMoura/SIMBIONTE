package com.example.simbionte.ui.chat

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simbionte.core.model.Chat
import com.example.simbionte.databinding.ItemMensagemEnviadaBinding
import com.example.simbionte.databinding.ItemMensagemRecebidaBinding

class ChatAdapter(private var mensagens: List<Chat> = mutableListOf()) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private companion object {
        const val TYPE_SENT = 1
        const val TYPE_RECEIVED = 0
    }

    override fun getItemViewType(position: Int): Int {
        return if (mensagens[position].enviada) TYPE_SENT else TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_SENT) {
            SentViewHolder(ItemMensagemEnviadaBinding.inflate(inflater, parent, false))
        } else {
            ReceivedViewHolder(ItemMensagemRecebidaBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = mensagens[position]
        if (holder is SentViewHolder) holder.bind(msg)
        else if (holder is ReceivedViewHolder) holder.bind(msg)
    }

    override fun getItemCount(): Int = mensagens.size

    fun adicionarMensagem(mensagem: Chat) {
        mensagens = mensagens + mensagem
        notifyItemInserted(mensagens.size - 1)
    }

    fun atualizarDados(novasMensagens: List<Chat>) {
        mensagens = novasMensagens
        notifyDataSetChanged()
    }

    class SentViewHolder(private val binding: ItemMensagemEnviadaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mensagem: Chat) {
            binding.textMensagem.text = mensagem.texto
            if (mensagem.relevanceScore > 0f) {
                binding.textScore.text = "%.1f".format(mensagem.relevanceScore)
                binding.textScore.visibility = android.view.View.VISIBLE
            } else {
                binding.textScore.visibility = android.view.View.GONE
            }
        }
    }

    class ReceivedViewHolder(private val binding: ItemMensagemRecebidaBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mensagem: Chat) {
            binding.textMensagem.text = mensagem.texto
            val tags = mensagem.tags?.takeIf { it.isNotBlank() }
            if (tags != null) {
                val tagList = tags.split(",").take(5).joinToString(", ")
                binding.textTags.text = "[$tagList]"
                binding.textTags.visibility = android.view.View.VISIBLE
            } else {
                binding.textTags.visibility = android.view.View.GONE
            }
            if (mensagem.relevanceScore > 0f) {
                binding.textScore.text = "score: %.1f".format(mensagem.relevanceScore)
                binding.textScore.visibility = android.view.View.VISIBLE
            } else {
                binding.textScore.visibility = android.view.View.GONE
            }
        }
    }
}
