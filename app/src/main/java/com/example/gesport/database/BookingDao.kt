package com.example.gesport.database

import androidx.room.*
import com.example.gesport.models.Booking
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(booking: Booking): Long

    @Query("SELECT * FROM reservas ORDER BY fecha DESC, horaInicio DESC")
    fun getAll(): Flow<List<Booking>>

    @Query("SELECT * FROM reservas WHERE usuarioId = :userId ORDER BY fecha DESC, horaInicio DESC")
    fun getByUser(userId: Int): Flow<List<Booking>>

    @Query("SELECT * FROM reservas WHERE pistaId = :courtId ORDER BY fecha DESC, horaInicio DESC")
    fun getByCourt(courtId: Int): Flow<List<Booking>>

    @Query("SELECT * FROM reservas WHERE equipoId = :teamId ORDER BY fecha DESC, horaInicio DESC")
    fun getByTeam(teamId: Int): Flow<List<Booking>>

    @Query("SELECT * FROM reservas WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Booking?

    @Query("""
        SELECT * FROM reservas
        WHERE pistaId = :courtId
          AND cancelada = 0
          AND horaInicio < :dayEnd
          AND horaFin > :dayStart
    """)
    suspend fun getByCourtAndDay(courtId: Int, dayStart: Long, dayEnd: Long): List<Booking>

    @Update
    suspend fun update(booking: Booking): Int

    @Query("UPDATE reservas SET cancelada = :cancelada WHERE id = :id")
    suspend fun setCancelada(id: Int, cancelada: Boolean)

    @Delete
    suspend fun delete(booking: Booking)
}