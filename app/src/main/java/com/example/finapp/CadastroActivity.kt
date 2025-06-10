package com.example.finapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.finapp.data.AppDatabase
import com.example.finapp.data.Transacao
import com.example.finapp.data.TipoTransacao
import kotlinx.coroutines.launch
import java.lang.NumberFormatException

class CadastroActivity : AppCompatActivity() {

    private lateinit var rgTipoOperacao: RadioGroup
    private lateinit var rbDebito: RadioButton
    private lateinit var rbCredito: RadioButton
    private lateinit var etDescricao: EditText
    private lateinit var etValor: EditText
    private lateinit var btnSalvarTransacao: Button

    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)
        rgTipoOperacao = findViewById(R.id.rgTipoOperacao)
        rbDebito = findViewById(R.id.rbDebito)
        rbCredito = findViewById(R.id.rbCredito)
        etDescricao = findViewById(R.id.etDescricao)
        etValor = findViewById(R.id.etValor)
        btnSalvarTransacao = findViewById(R.id.btnSalvarTransacao)
        database = AppDatabase.getDatabase(applicationContext)
        btnSalvarTransacao.setOnClickListener {
            salvarTransacao()
        }
    }

    private fun salvarTransacao() {
        val tipoSelecionado = if (rbDebito.isChecked) TipoTransacao.DEBITO else TipoTransacao.CREDITO
        val descricao = etDescricao.text.toString().trim()
        val valorStr = etValor.text.toString().trim()

        if (descricao.isEmpty()) {
            Toast.makeText(this, "Por favor, insira uma descrição.", Toast.LENGTH_SHORT).show()
            return
        }

        if (valorStr.isEmpty()) {
            Toast.makeText(this, "Por favor, insira um valor.", Toast.LENGTH_SHORT).show()
            return
        }

        val valor = try {
            valorStr.toDouble()
        } catch (e: NumberFormatException) {
            Toast.makeText(this, "Valor inválido. Use números e ponto para decimais.", Toast.LENGTH_SHORT).show()
            return
        }
        val novaTransacao = Transacao(
            tipo = tipoSelecionado,
            descricao = descricao,
            valor = valor
        )
        lifecycleScope.launch {
            try {
                database.transacaoDao().insertTransacao(novaTransacao)
                Toast.makeText(this@CadastroActivity, "Transação salva com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
                Toast.makeText(this@CadastroActivity, "Erro ao salvar transação: ${e.message}", Toast.LENGTH_LONG).show()
                e.printStackTrace()
            }
        }
    }
}