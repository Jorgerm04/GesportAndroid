package com.example.ges_sports.domain


import com.example.gesport.data.LoginRepository
import com.example.gesport.models.User

class LogicLogin {
    fun comprobarLogin(email: String, password: String): User {
        if (email.isBlank() || password.isBlank()) {
            throw IllegalArgumentException("Los campos no pueden estar vacíos.")
        }

        val user = LoginRepository.obtenerUsuarios()
            .find { it.email == email && it.password == password }
            ?: throw IllegalArgumentException("Email o contraseña incorrectos.")

        return user
    }
}