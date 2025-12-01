package com.example.gesport.ui.dashboard.GesUserScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gesport.models.User
import com.example.gesport.repository.UserRepository
import kotlinx.coroutines.launch

class GesUserViewModel(val userRepository: UserRepository) : ViewModel() {

    // Lista de usuarios para el listado
    private var _users by mutableStateOf<List<User>>(emptyList())
    val users: List<User> get() = _users

    // Filtro de rol
    private var _selectedRole by mutableStateOf<String?>(null)
    val selectedRole: String? get() = _selectedRole

    // Filtro de texto (nombre / email)
    private var _searchQuery by mutableStateOf("")
    val searchQuery: String get() = _searchQuery

    // Usuario actual para crear / editar
    private val _currentUser = MutableLiveData<User?>(null)
    val currentUser: LiveData<User?> = _currentUser

    // Evento para indicar que se ha guardado (crear/editar) correctamente
    private val _saveCompleted = MutableLiveData(false)
    val saveCompleted: LiveData<Boolean> = _saveCompleted

    init {
        viewModelScope.launch {
            refreshUsers()
        }
    }

    fun loadUsers() {
        viewModelScope.launch {
            refreshUsers()
        }
    }

    fun onRoleSelected(rol: String?) {
        _selectedRole = rol
        viewModelScope.launch {
            refreshUsers()
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery = query
        viewModelScope.launch {
            refreshUsers()
        }
    }

    private suspend fun refreshUsers() {
        val baseList = if (_selectedRole == null) {
            userRepository.getAllUsers()
        } else {
            userRepository.getUsersByRole(_selectedRole!!)
        }

        val q = _searchQuery.trim()
        _users = if (q.isBlank()) {
            baseList
        } else {
            val lower = q.lowercase()
            baseList.filter { user ->
                user.nombre.lowercase().contains(lower) ||
                        user.email.lowercase().contains(lower)
            }
        }
    }

    fun addUser(user: User) {
        viewModelScope.launch {
            userRepository.addUser(user)
            refreshUsers()
        }
    }

    fun loadUserById(userId: Int) {
        viewModelScope.launch {
            val user = userRepository.getUserById(userId)
            _currentUser.value = user
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
                val newUser = User(
                    id = 0,
                    nombre = nombre,
                    email = email,
                    password = password,
                    rol = rol
                )
                userRepository.addUser(newUser)
            } else {
                val existing = userRepository.getUserById(userId)
                if (existing != null) {
                    val updated = existing.copy(
                        nombre = nombre,
                        email = email,
                        password = password,
                        rol = rol
                    )
                    userRepository.updateUser(updated)
                }
            }

            refreshUsers()
            _saveCompleted.value = true
        }
    }

    fun onSaveCompletedHandled() {
        _saveCompleted.value = false
    }

    fun deleteUser(id: Int) {
        viewModelScope.launch {
            userRepository.deleteUser(id)
            refreshUsers()
        }
    }
}
