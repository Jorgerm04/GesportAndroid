package com.example.gesport.data

import com.example.gesport.database.BookingDao
import com.example.gesport.models.Booking
import com.example.gesport.repository.BookingRepository
import kotlinx.coroutines.flow.Flow

class RoomBookingRepository(private val bookingDao: BookingDao) : BookingRepository {

    override fun getAllBookings(): Flow<List<Booking>> = bookingDao.getAll()

    override fun getBookingsByUser(userId: Int): Flow<List<Booking>> = bookingDao.getByUser(userId)

    override fun getBookingsByCourt(courtId: Int): Flow<List<Booking>> = bookingDao.getByCourt(courtId)

    override fun getBookingsByTeam(teamId: Int): Flow<List<Booking>> = bookingDao.getByTeam(teamId)

    override suspend fun getBookingById(id: Int): Booking? = bookingDao.getById(id)

    override suspend fun addBooking(booking: Booking): Booking {
        val newId = bookingDao.insert(booking)
        return booking.copy(id = newId.toInt())
    }

    override suspend fun updateBooking(booking: Booking): Int = bookingDao.update(booking)

    override suspend fun setCancelada(id: Int, cancelada: Boolean) =
        bookingDao.setCancelada(id, cancelada)

    override suspend fun deleteBooking(id: Int): Boolean {
        val booking = bookingDao.getById(id) ?: return false
        bookingDao.delete(booking)
        return true
    }

    override suspend fun hasConflict(
        courtId: Int,
        horaInicio: Long,
        horaFin: Long,
        excludeId: Int?
    ): Boolean {
        val dayStart = horaInicio - 24 * 60 * 60 * 1000L
        val dayEnd   = horaFin   + 24 * 60 * 60 * 1000L
        val existing = bookingDao.getByCourtAndDay(courtId, dayStart, dayEnd)
        return existing.any { b ->
            b.id != (excludeId ?: -1) &&
                    !b.cancelada &&
                    horaInicio < b.horaFin &&
                    horaFin > b.horaInicio
        }
    }
}