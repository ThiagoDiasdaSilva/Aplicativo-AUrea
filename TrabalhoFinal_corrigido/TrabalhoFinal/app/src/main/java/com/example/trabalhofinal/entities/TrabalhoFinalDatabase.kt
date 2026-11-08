package com.example.trabalhofinal.entities

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Contact::class], version = 2, exportSchema = false)
abstract class TrabalhoFinalDatabase : RoomDatabase() {

    abstract fun contactDao(): ContactDao

    companion object {
        @Volatile
        private var INSTANCE: TrabalhoFinalDatabase? = null

        fun getInstance(context: Context): TrabalhoFinalDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    TrabalhoFinalDatabase::class.java,
                    "trabalho_final.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}