package com.example.gesport.repository

import com.example.gesport.models.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getAllUsers(): Flow<List<User>>
    fun getUsersByRole(role: String): Flow<List<User>>

    suspend fun getUserById(id: Int): User?

    suspend fun addUser(user: User): User

    suspend fun updateUser(user: User): Int

    suspend fun deleteUser(id: Int): Boolean

    suspend fun getUserByEmail(email: String): User?
}