package com.example.finapp

import android.icu.text.NumberFormat
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import java.util.Locale

class CadastroActivity : AppCompatActivity() {

    private lateinit var rgTipoOperacao: RadioGroup
    private lateinit var rbDebito: RadioButton
    private lateinit var rbCredito: RadioButton
    private lateinit var etDescricao: EditText
    private lateinit var etValor: EditText
    private lateinit var btnSalvarTransacao: Button
    private lateinit var database: AppDatabase
    private val currencyFormat: NumberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))
    private var isUpdatingText = false // Flag para evitar loop infinito do TextWatcher

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

        etValor.addTextChangedListener(object : TextWatcher {
            private var current = ""

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                if (isUpdatingText) {
                    return
                }
                isUpdatingText = true

                val cleanString = s.toString().replace("[^\\d]".toRegex(), "") // Remove tudo que não é dígito
                if (cleanString.isEmpty()) {
                    etValor.setText("")
                    isUpdatingText = false
                    return
                }

                val parsed = cleanString.toDouble() / 100
                val formatted = currencyFormat.format(parsed)
                etValor.setText(formatted)
                etValor.setSelection(formatted.length)

                isUpdatingText = false
            }
        })

        etValor.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                if (etValor.text.toString().isEmpty() && etValor.hint.toString().isNotEmpty()) {
                    return@setOnFocusChangeListener
                }
                val cleanString = etValor.text.toString().replace("[^\\d]".toRegex(), "")
                if (cleanString.isNotEmpty()) {
                    val parsed = cleanString.toDouble() / 100
                    val formatted = currencyFormat.format(parsed)
                    etValor.setText(formatted)
                }
            }
        }
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

        if (valorStr.isEmpty() || valorStr == currencyFormat.format(0.0)) {
            Toast.makeText(this, "Por favor, insira um valor válido.", Toast.LENGTH_SHORT).show()
            return
        }

        val parsedValor = try {
            currencyFormat.parse(valorStr)?.toDouble() ?: 0.0
        } catch (e: Exception) {
            Toast.makeText(this, "Valor inválido. Certifique-se de que o formato está correto.", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
            return
        }

        val novaTransacao = Transacao(
            tipo = tipoSelecionado,
            descricao = descricao,
            valor = parsedValor
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