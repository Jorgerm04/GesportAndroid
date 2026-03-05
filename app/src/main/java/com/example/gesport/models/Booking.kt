package com.example.gesport.models

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class BookingType(val label: String) {
    INDIVIDUAL("Individual"),
    EQUIPO("Equipo"),
    PARTIDO("Partido")
}

@Entity(tableName = "reservas")
data class Booking(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    // Tipo de reserva
    val tipo: String = BookingType.INDIVIDUAL.name,

    // Usuario principal (nulo en partidos)
    val usuarioId: Int? = null,
    val usuarioNombre: String? = null,

    // Pista
    val pistaId: Int,
    val pistaNombre: String,

    // Fechas
    val fecha: Long,
    val horaInicio: Long,
    val horaFin: Long,

    // Estado
    val cancelada: Boolean = false,
    val notas: String? = null,
    val createdAt: Long = System.currentTimeMillis(),

    // Equipo (tipo EQUIPO)
    val equipoId: Int? = null,
    val equipoNombre: String? = null,

    // Partido (tipo PARTIDO)
    val equipoLocalId: Int? = null,
    val equipoLocalNombre: String? = null,
    val equipoVisitanteId: Int? = null,
    val equipoVisitanteNombre: String? = null,
    val arbitroId: Int? = null,
    val arbitroNombre: String? = null,
    val puntosLocal: Int? = null,
    val puntosVisitante: Int? = null
) {
    val tipoEnum: BookingType get() = when (tipo) {
        "EQUIPO"  -> BookingType.EQUIPO
        "PARTIDO" -> BookingType.PARTIDO
        else      -> BookingType.INDIVIDUAL
    }
    val esDeEquipo: Boolean  get() = tipoEnum == BookingType.EQUIPO
    val esPartido: Boolean   get() = tipoEnum == BookingType.PARTIDO
}