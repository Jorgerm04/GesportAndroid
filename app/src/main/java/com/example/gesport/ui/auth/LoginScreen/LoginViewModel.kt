package com.example.gesport.ui.auth.LoginScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ges_sports.domain.LogicLogin
import com.example.gesport.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val logic = LogicLogin()

    private val _email = MutableLiveData("")
    private val _password = MutableLiveData("")
    private val _showPassword = MutableLiveData(false)
    private val _error = MutableLiveData("")

    private val _navigateToHome = MutableLiveData<String?>(null)
    private val _navigateToDashboard = MutableLiveData<String?>(null)

    val email: LiveData<String> = _email
    val password: LiveData<String> = _password
    val showPassword: LiveData<Boolean> = _showPassword
    val error: LiveData<String> = _error
    val navigateToHome: LiveData<String?> = _navigateToHome
    val navigateToDashboard: LiveData<String?> = _navigateToDashboard

    fun setEmail(newEmail: String) {
        _email.value = newEmail
    }

    fun setPassword(newPassword: String) {
        _password.value = newPassword
    }

    fun toggleShowPassword() {
        _showPassword.value = !(_showPassword.value ?: false)
    }

    fun clearError() {
        _error.value = ""
    }

    fun login() {
        val currentEmail = _email.value.orEmpty()
        val currentPassword = _password.value.orEmpty()

        if (currentEmail.isBlank() || currentPassword.isBlank()) {
            _error.value = "Los campos no pueden estar vacíos."
            return
        }

        viewModelScope.launch {
            // Buscamos en Room por email
            val user = userRepository.getUserByEmail(currentEmail)

            if (user != null && user.password == currentPassword) {
                _error.value = ""
                // Redirigir según el rol (Asegúrate de que el String coincida con tu BD)
                if (user.rol == "ADMIN_DEPORTIVO" || user.rol == "ADMIN") {
                    _navigateToDashboard.value = user.nombre
                } else {
                    _navigateToHome.value = user.nombre
                }
            } else {
                _error.value = "Email o contraseña incorrectos."
            }
        }
    }

    fun onNavigationDone() {
        _navigateToHome.value = null
        _navigateToDashboard.value = null
    }
}