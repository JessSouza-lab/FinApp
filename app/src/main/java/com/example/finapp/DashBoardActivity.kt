package com.example.finapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val btnCadastroOperacoes: Button = findViewById(R.id.btnCadastroOperacoes)
        val btnExtrato: Button = findViewById(R.id.btnExtrato)
        val btnSair: Button = findViewById(R.id.btnSair)

        btnCadastroOperacoes.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            startActivity(intent)
        }

        btnExtrato.setOnClickListener {
            val intent = Intent(this, ExtratoActivity::class.java)
            startActivity(intent)
        }

        btnSair.setOnClickListener {
            finishAffinity()
        }
    }
}