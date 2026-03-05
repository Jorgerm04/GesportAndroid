package com.example.gesport.repository

import com.example.gesport.models.Team
import kotlinx.coroutines.flow.Flow

interface TeamRepository {
    fun getAllTeams(): Flow<List<Team>>
    fun getTeamsByCoach(coachId: Int): Flow<List<Team>>
    suspend fun getTeamById(id: Int): Team?
    suspend fun addTeam(team: Team): Team
    suspend fun updateTeam(team: Team): Int
    suspend fun deleteTeam(id: Int): Boolean
    suspend fun addPlayer(teamId: Int, userId: Int): Boolean
    suspend fun removePlayer(teamId: Int, userId: Int): Boolean
    suspend fun setCoach(teamId: Int, coachId: Int?, coachNombre: String?): Boolean
}