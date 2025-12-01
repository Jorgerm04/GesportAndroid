package com.example.gesport.repository

import com.example.gesport.models.User

interface UserRepository {

    /** Obtener una lista con todos los usuarios */
    suspend fun getAllUsers(): List<User>

    /** Obtener una lista de usaurios por rol */
    suspend fun getUsersByRole(rol: String): List<User>

    /** Obtener un usuario por id */
    suspend fun getUserById(id: Int): User?

    /** Crear usuario nuevo */
    suspend fun addUser(user: User): User

    /** Actualizar usuario existente (true si se ha podido actualizar) */
    suspend fun updateUser(user: User): Boolean

    /** Eliminar usuario por id (true si se ha eliminado alguno) */
    suspend fun deleteUser(id: Int): Boolean
}