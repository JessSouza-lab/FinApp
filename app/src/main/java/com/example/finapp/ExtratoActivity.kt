package com.example.finapp

import android.os.Bundle
import android.icu.text.NumberFormat
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.finapp.adapter.TransacaoAdapter
import com.example.finapp.data.AppDatabase
import com.example.finapp.data.Transacao
import com.example.finapp.data.TipoTransacao
import kotlinx.coroutines.launch
import java.util.Locale

class ExtratoActivity : AppCompatActivity() {

    private lateinit var tvSaldo: TextView
    private lateinit var rgFiltro: RadioGroup
    private lateinit var recyclerViewTransacoes: RecyclerView
    private lateinit var btnAddTransacao: Button
    private lateinit var btnSair: Button

    private lateinit var transacaoAdapter: TransacaoAdapter
    private lateinit var database: AppDatabase

    private var currentFilter: TipoTransacao? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_extrato)

        tvSaldo = findViewById(R.id.tvSaldo)
        rgFiltro = findViewById(R.id.rgFiltro)
        recyclerViewTransacoes = findViewById(R.id.recyclerViewTransacoes)
        btnAddTransacao = findViewById(R.id.btnAddTransacao)
        btnSair = findViewById(R.id.btnSair)

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

        btnAddTransacao.setOnClickListener {
            val testTransacao = Transacao(
                tipo = if ((0..1).random() == 0) TipoTransacao.CREDITO else TipoTransacao.DEBITO,
                descricao = if ((0..1).random() == 0) "Salário" else "Conta de Luz",
                valor = (50..500).random().toDouble(),
                data = System.currentTimeMillis() - (0..30).random() * 24 * 60 * 60 * 1000
            )
            lifecycleScope.launch {
                database.transacaoDao().insertTransacao(testTransacao)
            }
        }

        btnSair.setOnClickListener {
            finishAffinity()
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