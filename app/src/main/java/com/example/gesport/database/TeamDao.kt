package com.example.gesport.database

import androidx.room.*
import com.example.gesport.models.Team
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(team: Team): Long

    @Query("SELECT * FROM equipos ORDER BY nombre ASC")
    fun getAll(): Flow<List<Team>>

    @Query("SELECT * FROM equipos WHERE entrenadorId = :coachId")
    fun getByCoach(coachId: Int): Flow<List<Team>>

    @Query("SELECT * FROM equipos WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Team?

    @Update
    suspend fun update(team: Team): Int

    @Delete
    suspend fun delete(team: Team)
}