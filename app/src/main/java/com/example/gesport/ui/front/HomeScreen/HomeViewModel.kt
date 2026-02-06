package com.example.gesport.ui.front.HomeScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gesport.models.User
import com.example.gesport.repository.UserRepository
import kotlinx.coroutines.launch

class HomeViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _user = MutableLiveData<User?>()
    val user: LiveData<User?> = _user

    // Función para cargar los datos del usuario desde Room
    fun loadUser(userName: String) {
        viewModelScope.launch {
            // Buscamos en la lista de usuarios el que coincida con el nombre
            // (O podrías añadir un método específico en el Repo para esto)
            userRepository.getAllUsers().collect { lista ->
                _user.value = lista.find { it.nombre == userName }
            }
        }
    }

    fun logout(onLogout: () -> Unit) {

        onLogout()
    }
}