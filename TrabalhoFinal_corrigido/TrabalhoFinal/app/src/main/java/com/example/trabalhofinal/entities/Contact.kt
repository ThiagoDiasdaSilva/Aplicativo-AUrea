package com.example.trabalhofinal.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "contact")
data class Contact(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,            // id da sessão, chave primária
    val nome: String,           // nome opcional que o usuário escolhe salvar
    val hectare: Float,         // número de hectares
    val pastagem: String,        // tipo de pastagem
    val areas: String
)
