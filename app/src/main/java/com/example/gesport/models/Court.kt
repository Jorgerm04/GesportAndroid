package com.example.gesport.models

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CourtType(val label: String) {
    PADEL("Pádel"),
    FUTBOL("Fútbol"),
    BALONCESTO("Baloncesto"),
    TENIS("Tenis");

    companion object {
        fun fromString(value: String?): CourtType {
            return when (value?.uppercase()) {
                "FUTBOL"     -> FUTBOL
                "BALONCESTO" -> BALONCESTO
                "TENIS"      -> TENIS
                else         -> PADEL
            }
        }
    }
}

@Entity(tableName = "pistas")
data class Court(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val tipo: String,
    val descripcion: String = "",
    val activa: Boolean = true,
    val precioPorHora: Double = 0.0
) {
    val tipoEnum: CourtType get() = CourtType.fromString(tipo)
}