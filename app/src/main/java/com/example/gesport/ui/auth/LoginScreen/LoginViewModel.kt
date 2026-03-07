package com.example.gesport.ui.auth.LoginScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gesport.repository.UserRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _email        = MutableLiveData("")
    private val _password     = MutableLiveData("")
    private val _showPassword = MutableLiveData(false)
    private val _error        = MutableLiveData("")

    // Emite la ruta destino: "home/{id}" o "dashboard/{id}"
    private val _navigateTo = MutableLiveData<String?>(null)

    val email:        LiveData<String>  = _email
    val password:     LiveData<String>  = _password
    val showPassword: LiveData<Boolean> = _showPassword
    val error:        LiveData<String>  = _error
    val navigateTo:   LiveData<String?> = _navigateTo

    fun setEmail(v: String)    { _email.value    = v }
    fun setPassword(v: String) { _password.value = v }
    fun toggleShowPassword()   { _showPassword.value = !(_showPassword.value ?: false) }
    fun clearError()           { _error.value = "" }

    fun login() {
        val email    = _email.value.orEmpty()
        val password = _password.value.orEmpty()

        if (email.isBlank() || password.isBlank()) {
            _error.value = "Los campos no pueden estar vacíos."
            return
        }

        viewModelScope.launch {
            val user = userRepository.getUserByEmail(email)
            if (user != null && user.password == password) {
                _error.value = ""
                _navigateTo.value = if (user.rol == "ADMIN") "dashboard/${user.id}" else "home/${user.id}"
            } else {
                _error.value = "Email o contraseña incorrectos."
            }
        }
    }

    fun onNavigationDone() { _navigateTo.value = null }
}