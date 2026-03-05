package com.example.gesport.ui.dashboard.GesTeamsScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gesport.models.Team
import com.example.gesport.models.User
import com.example.gesport.repository.TeamRepository
import com.example.gesport.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class GesTeamViewModel(
    private val teamRepository: TeamRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private var _teams by mutableStateOf<List<Team>>(emptyList())
    val teams: List<Team> get() = _teams

    private var _searchQuery by mutableStateOf("")
    val searchQuery: String get() = _searchQuery

    private var _allUsers by mutableStateOf<List<User>>(emptyList())
    val allUsers: List<User> get() = _allUsers

    private val _currentTeam = MutableLiveData<Team?>(null)
    val currentTeam: LiveData<Team?> = _currentTeam

    private val _saveCompleted = MutableLiveData(false)
    val saveCompleted: LiveData<Boolean> = _saveCompleted

    init {
        viewModelScope.launch {
            teamRepository.getAllTeams().collect { lista ->
                _teams = applyFilter(lista)
            }
        }
        viewModelScope.launch {
            userRepository.getAllUsers().collect { lista ->
                _allUsers = lista
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery = query
        viewModelScope.launch { refreshTeams() }
    }

    private suspend fun refreshTeams() {
        val base = teamRepository.getAllTeams().first()
        _teams = applyFilter(base)
    }

    private fun applyFilter(base: List<Team>): List<Team> {
        val q = _searchQuery.trim().lowercase()
        return if (q.isBlank()) base
        else base.filter {
            it.nombre.lowercase().contains(q) ||
                    it.descripcion.lowercase().contains(q)
        }
    }

    fun loadTeamById(id: Int) {
        viewModelScope.launch {
            _currentTeam.value = teamRepository.getTeamById(id)
        }
    }

    fun saveTeam(
        teamId: Int?,
        nombre: String,
        descripcion: String,
        entrenadorId: Int?,
        entrenadorNombre: String?,
        jugadoresIds: List<Int>
    ) {
        viewModelScope.launch {
            val jugadoresCsv = jugadoresIds.joinToString(",")
            if (teamId == null) {
                teamRepository.addTeam(
                    Team(
                        nombre = nombre,
                        descripcion = descripcion,
                        entrenadorId = entrenadorId,
                        entrenadorNombre = entrenadorNombre,
                        jugadoresIds = jugadoresCsv
                    )
                )
            } else {
                val existing = teamRepository.getTeamById(teamId)
                if (existing != null) {
                    teamRepository.updateTeam(
                        existing.copy(
                            nombre = nombre,
                            descripcion = descripcion,
                            entrenadorId = entrenadorId,
                            entrenadorNombre = entrenadorNombre,
                            jugadoresIds = jugadoresCsv
                        )
                    )
                }
            }
            refreshTeams()
            _saveCompleted.value = true
        }
    }

    fun deleteTeam(id: Int) {
        viewModelScope.launch {
            teamRepository.deleteTeam(id)
            refreshTeams()
        }
    }

    fun onSaveCompletedHandled() {
        _saveCompleted.value = false
    }
}