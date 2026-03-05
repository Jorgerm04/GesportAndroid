package com.example.gesport.data

import com.example.gesport.database.CourtDao
import com.example.gesport.models.Court
import com.example.gesport.repository.CourtRepository
import kotlinx.coroutines.flow.Flow

class RoomCourtRepository(private val courtDao: CourtDao) : CourtRepository {

    override fun getAllCourts(): Flow<List<Court>> = courtDao.getAll()

    override fun getActiveCourts(): Flow<List<Court>> = courtDao.getActive()

    override suspend fun getCourtById(id: Int): Court? = courtDao.getById(id)

    override suspend fun addCourt(court: Court): Court {
        val newId = courtDao.insert(court)
        return court.copy(id = newId.toInt())
    }

    override suspend fun updateCourt(court: Court): Int = courtDao.update(court)

    override suspend fun deleteCourt(id: Int): Boolean {
        val court = courtDao.getById(id) ?: return false
        courtDao.delete(court)
        return true
    }
}