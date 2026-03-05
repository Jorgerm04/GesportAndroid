package com.example.gesport.repository

import com.example.gesport.models.Court
import kotlinx.coroutines.flow.Flow

interface CourtRepository {
    fun getAllCourts(): Flow<List<Court>>
    fun getActiveCourts(): Flow<List<Court>>
    suspend fun getCourtById(id: Int): Court?
    suspend fun addCourt(court: Court): Court
    suspend fun updateCourt(court: Court): Int
    suspend fun deleteCourt(id: Int): Boolean
}