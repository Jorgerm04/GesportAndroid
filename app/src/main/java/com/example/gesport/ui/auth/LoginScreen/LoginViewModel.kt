package com.example.gesport.ui.auth.LoginScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.ges_sports.domain.LogicLogin

class LoginViewModel : ViewModel() {

    private val logic = LogicLogin()

    private val _email = MutableLiveData("")
    private val _password = MutableLiveData("")
    private val _showPassword = MutableLiveData(false)
    private val _error = MutableLiveData("")

    // Navegación según rol
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

    /**
     * Lógica de login. Si es correcto, decide ruta según rol.
     * ADMIN_DEPORTIVO -> dashboard
     * cualquier otro  -> home
     */
    fun login() {
        val currentEmail = _email.value.orEmpty()
        val currentPassword = _password.value.orEmpty()

        try {
            val user = logic.comprobarLogin(currentEmail, currentPassword)
            _error.value = ""

            // ⬇️ AJUSTA el nombre de la propiedad de rol si en tu modelo se llama distinto
            if (user.rol == "ADMIN_DEPORTIVO") {
                _navigateToDashboard.value = user.nombre
            } else {
                _navigateToHome.value = user.nombre
            }

        } catch (e: IllegalArgumentException) {
            val errorMessage = e.message
            _error.value = errorMessage ?: "Error en el inicio de sesión"
        }
    }

    /**
     * Llamar desde la vista después de navegar para evitar navegar varias veces.
     */
    fun onNavigationDone() {
        _navigateToHome.value = null
        _navigateToDashboard.value = null
    }
}