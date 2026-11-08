package com.example.trabalhofinal.entities

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ContactDao {

    @Query("SELECT * FROM contact")
    fun findAll(): Flow<List<Contact>>

    @Query("SELECT * FROM contact ORDER BY nome ASC")
    fun getContatosPorNome(): Flow<List<Contact>>

    @Query("SELECT * FROM contact WHERE id = :id")
    suspend fun getById(id: Int): Contact?

    @Upsert
    suspend fun upsertContact(contact: Contact)

    @Delete
    suspend fun deleteContact(contact: Contact)
}
