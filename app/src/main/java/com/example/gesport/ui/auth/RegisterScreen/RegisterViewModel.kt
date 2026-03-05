package com.example.gesport.ui.auth.RegisterScreen

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gesport.data.RoomUserRepository
import com.example.gesport.database.AppDatabase
import com.example.gesport.models.User
import kotlinx.coroutines.launch

class RegisterViewModel(private val userRepository: RoomUserRepository) : ViewModel() {

    var isLoading   by mutableStateOf(false)    ; private set
    var errorMsg    by mutableStateOf<String?>(null) ; private set
    var registered  by mutableStateOf(false)    ; private set

    fun register(nombre: String, email: String, password: String) {
        // Validaciones básicas
        if (nombre.isBlank()) { errorMsg = "El nombre no puede estar vacío"; return }
        if (email.isBlank() || !email.contains("@")) { errorMsg = "Email no válido"; return }
        if (password.length < 4) { errorMsg = "La contraseña debe tener al menos 4 caracteres"; return }

        viewModelScope.launch {
            isLoading = true
            errorMsg  = null

            // Comprobar si el email ya existe
            val existing = userRepository.getUserByEmail(email.trim())
            if (existing != null) {
                errorMsg = "Ya existe una cuenta con ese email"
                isLoading = false
                return@launch
            }

            // Crear usuario como JUGADOR por defecto
            userRepository.addUser(
                User(
                    nombre   = nombre.trim(),
                    email    = email.trim().lowercase(),
                    password = password,
                    rol      = "JUGADOR"
                )
            )
            isLoading  = false
            registered = true
        }
    }

    fun clearError() { errorMsg = null }
}