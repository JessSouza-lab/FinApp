package com.example.finapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Transacao::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun transacaoDao(): TransacaoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "finance_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
