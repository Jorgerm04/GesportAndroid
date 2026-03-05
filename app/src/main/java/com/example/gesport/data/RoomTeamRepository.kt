package com.example.gesport.data

import com.example.gesport.database.TeamDao
import com.example.gesport.models.Team
import com.example.gesport.repository.TeamRepository
import kotlinx.coroutines.flow.Flow

class RoomTeamRepository(private val teamDao: TeamDao) : TeamRepository {

    override fun getAllTeams(): Flow<List<Team>> = teamDao.getAll()

    override fun getTeamsByCoach(coachId: Int): Flow<List<Team>> = teamDao.getByCoach(coachId)

    override suspend fun getTeamById(id: Int): Team? = teamDao.getById(id)

    override suspend fun addTeam(team: Team): Team {
        val newId = teamDao.insert(team)
        return team.copy(id = newId.toInt())
    }

    override suspend fun updateTeam(team: Team): Int = teamDao.update(team)

    override suspend fun deleteTeam(id: Int): Boolean {
        val team = teamDao.getById(id) ?: return false
        teamDao.delete(team)
        return true
    }

    override suspend fun addPlayer(teamId: Int, userId: Int): Boolean {
        val team = teamDao.getById(teamId) ?: return false
        val ids = team.getJugadoresIdsList().toMutableList()
        if (!ids.contains(userId)) {
            ids.add(userId)
            teamDao.update(team.copy(jugadoresIds = ids.joinToString(",")))
        }
        return true
    }

    override suspend fun removePlayer(teamId: Int, userId: Int): Boolean {
        val team = teamDao.getById(teamId) ?: return false
        val ids = team.getJugadoresIdsList().toMutableList()
        ids.remove(userId)
        teamDao.update(team.copy(jugadoresIds = ids.joinToString(",")))
        return true
    }

    override suspend fun setCoach(teamId: Int, coachId: Int?, coachNombre: String?): Boolean {
        val team = teamDao.getById(teamId) ?: return false
        teamDao.update(team.copy(entrenadorId = coachId, entrenadorNombre = coachNombre))
        return true
    }
}