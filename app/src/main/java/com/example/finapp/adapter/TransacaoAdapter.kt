package com.example.finapp.adapter

import android.icu.text.NumberFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.finapp.R
import com.example.finapp.data.Transacao
import com.example.finapp.data.TipoTransacao
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransacaoAdapter(private var transacoes: List<Transacao>) :
    RecyclerView.Adapter<TransacaoAdapter.TransacaoViewHolder>() {

    class TransacaoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val ivTipoTransacao: ImageView = itemView.findViewById(R.id.ivTipoTransacao)
        val tvDescricaoTransacao: TextView = itemView.findViewById(R.id.tvDescricaoTransacao)
        val tvValorTransacao: TextView = itemView.findViewById(R.id.tvValorTransacao)
        val tvDataTransacao: TextView = itemView.findViewById(R.id.tvDataTransacao)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransacaoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transacao, parent, false)
        return TransacaoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransacaoViewHolder, position: Int) {
        val transacao = transacoes[position]

        holder.tvDescricaoTransacao.text = transacao.descricao

        val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
        val valorFormatado = currencyFormat.format(transacao.valor)

        holder.tvValorTransacao.text = valorFormatado

        val colorResId = when (transacao.tipo) {
            TipoTransacao.CREDITO -> android.R.color.holo_green_dark
            TipoTransacao.DEBITO -> android.R.color.holo_red_dark
        }
        holder.tvValorTransacao.setTextColor(ContextCompat.getColor(holder.itemView.context, colorResId))

            val imageResId = when (transacao.tipo) {
            TipoTransacao.CREDITO -> R.drawable.ic_credito
            TipoTransacao.DEBITO -> R.drawable.ic_debito
        }
        holder.ivTipoTransacao.setImageResource(imageResId)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale("pt", "BR"))
        holder.tvDataTransacao.text = dateFormat.format(Date(transacao.data))
    }

    override fun getItemCount(): Int = transacoes.size

    fun updateTransacoes(newTransacoes: List<Transacao>) {
        this.transacoes = newTransacoes
        notifyDataSetChanged() // Notifica o RecyclerView que os dados mudaram
    }
}