package com.example.gesport.data;

import androidx.compose.runtime.mutableStateOf
import com.example.gesport.models.User;
import com.example.gesport.repository.UserRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import android.content.Context
import android.widget.Toast
import java.io.File

/*class JsonUserRepository(private val context: Context): UserRepository{


    private val jsonFile = File(context.filesDir,"users.json") //FICHERO DINAMICO
     val jsonString = context.assets.open("users.json") //FICHERO DE INICIO CARGADO DESDE ASSETS
        .bufferedReader()
        .use {it.readText()}
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }
    private val users = mutableListOf<User>()
    private var nextId: Int = 1

    init {
        loadFile()
    }

    private fun loadFile() {
        val text = jsonFile.readText()
        if (text.isNotBlank()) {
            Toast.makeText(context,"CARGA DATOS DESDE EL MÓVIL",Toast.LENGTH_LONG)
        } else {
            loadFromAssets()
            saveToFile()
        }
    }

    private fun loadFromAssets() {
        if (jsonString.isEmpty()) {
            users.clear()
            nextId = 1
            return
        }

        if (jsonString.isBlank()) {
            users.clear()
            nextId = 1
            return
        }

        val loadedUsers = json.decodeFromString<List<User>>(jsonString)
        users.clear()
        users.addAll(loadedUsers)

        nextId = (users.maxOfOrNull { it.id } ?: 0) + 1
    }



    private fun saveToFile(){
        val text = json.encodeToString(users)
    }
    override suspend fun getAllUsers(): List<User> {
        return users.toList()
    }

    override suspend fun getUsersByRole(rol: String): List<User> {
        return users.filter {it.rol == rol}
    }

    override suspend fun getUserById(id: Int): User? {
        return users.find {it.id == id}
    }

    private fun getNewId(): Int {
        return (users.maxOfOrNull { it.id } ?: 0) + 1
    }

    override suspend fun addUser(user: User): User {
        val newId = getNewId()
        val newUser = user.copy(id = newId)
        users.add(newUser)
        saveToFile()
        return newUser
    }

    override suspend fun updateUser(user: User): Boolean {
        val index = users.indexOfFirst { it.id == user.id }
        return if (index != -1) {
            users[index] = user
            saveToFile()
            true
        } else {
            false
        }
    }

    override suspend fun deleteUser(id: Int): Boolean {
        val removed = users.removeIf { it.id == id }

        if (removed){
            saveToFile()
        }
        return removed
    }
}*/
