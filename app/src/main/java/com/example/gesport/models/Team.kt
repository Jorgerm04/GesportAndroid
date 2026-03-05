package com.example.gesport.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "equipos")
data class Team(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val sport: String = "",
    val descripcion: String = "",
    val entrenadorId: Int? = null,
    val entrenadorNombre: String? = null,
    val jugadoresIds: String = ""   // IDs separados por coma: "1,2,3"
) {
    fun getJugadoresIdsList(): List<Int> =
        if (jugadoresIds.isBlank()) emptyList()
        else jugadoresIds.split(",").mapNotNull { it.trim().toIntOrNull() }
}