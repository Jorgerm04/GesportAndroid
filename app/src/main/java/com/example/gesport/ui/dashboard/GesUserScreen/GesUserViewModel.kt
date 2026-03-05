package com.example.gesport.ui.dashboard.GesUserScreen

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

class GesUserViewModel(
    val userRepository: UserRepository,
    val teamRepository: TeamRepository
) : ViewModel() {

    private var _users by mutableStateOf<List<User>>(emptyList())
    val users: List<User> get() = _users

    private var _selectedRole by mutableStateOf<String?>(null)
    val selectedRole: String? get() = _selectedRole

    private var _searchQuery by mutableStateOf("")
    val searchQuery: String get() = _searchQuery

    private val _currentUser = MutableLiveData<User?>(null)
    val currentUser: LiveData<User?> = _currentUser

    private val _saveCompleted = MutableLiveData(false)
    val saveCompleted: LiveData<Boolean> = _saveCompleted

    private val _equipoAsociado = MutableLiveData<Team?>(null)
    val equipoAsociado: LiveData<Team?> = _equipoAsociado

    private val _loadingEquipo = MutableLiveData(false)
    val loadingEquipo: LiveData<Boolean> = _loadingEquipo

    init {
        viewModelScope.launch {
            userRepository.getAllUsers().collect { lista ->
                _users = lista
            }
        }
    }

    fun loadUsers() {
        viewModelScope.launch { refreshUsers() }
    }

    fun onRoleSelected(rol: String?) {
        _selectedRole = rol
        viewModelScope.launch { refreshUsers() }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery = query
        viewModelScope.launch { refreshUsers() }
    }

    private suspend fun refreshUsers() {
        val baseList = if (_selectedRole == null) {
            userRepository.getAllUsers().first()
        } else {
            userRepository.getUsersByRole(_selectedRole!!).first()
        }
        val q = _searchQuery.trim()
        _users = if (q.isBlank()) baseList
        else {
            val lower = q.lowercase()
            baseList.filter {
                it.nombre.lowercase().contains(lower) ||
                        it.email.lowercase().contains(lower)
            }
        }
    }

    fun loadUserById(userId: Int) {
        viewModelScope.launch {
            _currentUser.value = userRepository.getUserById(userId)
        }
    }

    fun loadEquipoAsociado(userId: Int, rol: String) {
        viewModelScope.launch {
            _loadingEquipo.value = true
            _equipoAsociado.value = null
            val todosEquipos = teamRepository.getAllTeams().first()
            _equipoAsociado.value = if (rol == "ENTRENADOR") {
                todosEquipos.firstOrNull { it.entrenadorId == userId }
            } else {
                todosEquipos.firstOrNull { it.getJugadoresIdsList().contains(userId) }
            }
            _loadingEquipo.value = false
        }
    }

    fun saveUser(
        userId: Int?,
        nombre: String,
        email: String,
        password: String,
        rol: String
    ) {
        viewModelScope.launch {
            if (userId == null) {
                userRepository.addUser(
                    User(id = 0, nombre = nombre, email = email, password = password, rol = rol)
                )
            } else {
                val existing = userRepository.getUserById(userId)
                if (existing != null) {
                    userRepository.updateUser(
                        existing.copy(nombre = nombre, email = email, password = password, rol = rol)
                    )
                }
            }
            refreshUsers()
            _saveCompleted.value = true
        }
    }

    fun onSaveCompletedHandled() { _saveCompleted.value = false }

    fun deleteUser(id: Int) {
        viewModelScope.launch {
            userRepository.deleteUser(id)
            refreshUsers()
        }
    }
}