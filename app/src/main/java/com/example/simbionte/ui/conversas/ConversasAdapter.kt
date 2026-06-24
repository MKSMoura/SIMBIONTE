package com.example.simbionte.ui.conversas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.simbionte.core.model.Conversas
import com.example.simbionte.databinding.ItemConversasBinding

class ConversasAdapter(
    private var conversas: List<Conversas>,
    private val onConversaClick: (Conversas) -> Unit
) : RecyclerView.Adapter<ConversasAdapter.ConversaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversaViewHolder {
        val binding = ItemConversasBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ConversaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ConversaViewHolder, position: Int) {
        val conversa = conversas[position]
        holder.bind(conversa)
        holder.itemView.setOnClickListener { onConversaClick(conversa) }
    }

    override fun getItemCount(): Int = conversas.size

    fun atualizarDados(novasConversas: List<Conversas>) {
        conversas = novasConversas
        notifyDataSetChanged()
    }

    class ConversaViewHolder(private val binding: ItemConversasBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(conversa: Conversas) {
            binding.textTituloConversa.text = conversa.nome
            binding.textLastMessage.text = conversa.ultimaMensagem
            binding.textTime.text = conversa.hora
            
            if (conversa.naoLidas > 0) {
                binding.badgeUnread.visibility = View.VISIBLE
                binding.badgeUnread.text = conversa.naoLidas.toString()
            } else {
                binding.badgeUnread.visibility = View.GONE
            }
        }
    }
}
