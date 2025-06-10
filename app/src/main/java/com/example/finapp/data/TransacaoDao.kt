package com.example.finapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TransacaoDao {
    @Insert
    suspend fun insertTransacao(transacao: Transacao): Long

    @Query("SELECT * FROM transacoes ORDER BY data DESC")
    fun getAllTransacoes(): Flow<List<Transacao>>

    @Query("SELECT * FROM transacoes WHERE tipo = 'DEBITO' ORDER BY data DESC")
    fun getDebitoTransacoes(): Flow<List<Transacao>>

    @Query("SELECT * FROM transacoes WHERE tipo = 'CREDITO' ORDER BY data DESC")
    fun getCreditoTransacoes(): Flow<List<Transacao>>

    @Query("SELECT SUM(CASE WHEN tipo = 'CREDITO' THEN valor ELSE 0 END) - SUM(CASE WHEN tipo = 'DEBITO' THEN valor ELSE 0 END) FROM transacoes")
    fun getSaldoTotal(): Flow<Double?> // Use Flow<Double?> para lidar com BD vazio
}