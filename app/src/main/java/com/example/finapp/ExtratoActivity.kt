package com.example.finapp

import android.icu.text.NumberFormat
import android.os.Bundle
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.finapp.adapter.TransacaoAdapter
import com.example.finapp.data.AppDatabase
import com.example.finapp.data.TipoTransacao
import kotlinx.coroutines.launch
import java.util.Locale

class ExtratoActivity : AppCompatActivity() {

    private lateinit var tvSaldo: TextView
    private lateinit var rgFiltro: RadioGroup
    private lateinit var recyclerViewTransacoes: RecyclerView
    private lateinit var transacaoAdapter: TransacaoAdapter
    private lateinit var database: AppDatabase
    private var currentFilter: TipoTransacao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_extrato)

        tvSaldo = findViewById(R.id.tvSaldo)
        rgFiltro = findViewById(R.id.rgFiltro)
        recyclerViewTransacoes = findViewById(R.id.recyclerViewTransacoes)

        database = AppDatabase.getDatabase(applicationContext)

        transacaoAdapter = TransacaoAdapter(emptyList())
        recyclerViewTransacoes.adapter = transacaoAdapter

        observeTransacoes()
        observeSaldo()

        rgFiltro.setOnCheckedChangeListener { _, checkedId ->
            currentFilter = when (checkedId) {
                R.id.rbTodas -> null
                R.id.rbDebitos -> TipoTransacao.DEBITO
                R.id.rbCreditos -> TipoTransacao.CREDITO
                else -> null
            }
            observeTransacoes()
        }
    }

    private fun observeTransacoes() {
        lifecycleScope.launch {
            when (currentFilter) {
                null -> database.transacaoDao().getAllTransacoes()
                TipoTransacao.DEBITO -> database.transacaoDao().getDebitoTransacoes()
                TipoTransacao.CREDITO -> database.transacaoDao().getCreditoTransacoes()
            }.collect { transacoes ->
                transacaoAdapter.updateTransacoes(transacoes)
            }
        }
    }

    private fun observeSaldo() {
        lifecycleScope.launch {
            database.transacaoDao().getSaldoTotal().collect { saldo ->
                val currencyFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
                val saldoFormatado = currencyFormat.format(saldo ?: 0.0)
                tvSaldo.text = "Saldo: $saldoFormatado"
            }
        }
    }
}