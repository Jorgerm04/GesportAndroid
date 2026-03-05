package com.example.gesport.database

import androidx.room.*
import com.example.gesport.models.Court
import kotlinx.coroutines.flow.Flow

@Dao
interface CourtDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(court: Court): Long

    @Query("SELECT * FROM pistas ORDER BY nombre ASC")
    fun getAll(): Flow<List<Court>>

    @Query("SELECT * FROM pistas WHERE activa = 1 ORDER BY nombre ASC")
    fun getActive(): Flow<List<Court>>

    @Query("SELECT * FROM pistas WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Court?

    @Update
    suspend fun update(court: Court): Int

    @Delete
    suspend fun delete(court: Court)
}