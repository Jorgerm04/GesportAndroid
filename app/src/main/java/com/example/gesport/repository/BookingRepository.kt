package com.example.gesport.repository

import com.example.gesport.models.Booking
import kotlinx.coroutines.flow.Flow

interface BookingRepository {
    fun getAllBookings(): Flow<List<Booking>>
    fun getBookingsByUser(userId: Int): Flow<List<Booking>>
    fun getBookingsByCourt(courtId: Int): Flow<List<Booking>>
    fun getBookingsByTeam(teamId: Int): Flow<List<Booking>>
    suspend fun getBookingById(id: Int): Booking?
    suspend fun addBooking(booking: Booking): Booking
    suspend fun updateBooking(booking: Booking): Int
    suspend fun setCancelada(id: Int, cancelada: Boolean)
    suspend fun deleteBooking(id: Int): Boolean
    suspend fun hasConflict(
        courtId: Int,
        horaInicio: Long,
        horaFin: Long,
        excludeId: Int? = null
    ): Boolean
}