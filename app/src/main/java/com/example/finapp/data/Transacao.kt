package com.example.finapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "transacoes")
data class Transacao(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tipo: TipoTransacao,
    val descricao: String,
    val valor: Double,
    val data: Long = System.currentTimeMillis()
) : Serializable

enum class TipoTransacao {
    DEBITO,
    CREDITO
}
