package com.example.gesport.data

import com.example.gesport.models.User
import com.example.gesport.repository.UserRepository
/*
object DataUserRepository : UserRepository {

    private val users = mutableListOf(
        User(
            id = 1,
            nombre = "Ana Pérez",
            email = "ana1@gmail.com",
            password = "1234",
            rol = "ADMIN_DEPORTIVO"
        ),
        User(
            id = 2,
            nombre = "Pedro Caselles",
            email = "pedro2@gmail.com",
            password = "1234",
            rol ="ENTRENADOR"
        ),
        User(
            id = 3,
            nombre = "Pepa Ferrández",
            email = "pepa3@gmail.com",
            password = "1234",
            rol = "JUGADOR"
        ),
        User(
            id = 4,
            nombre = "Pablo Teruel",
            email = "pablo4@gmail.com",
            password = "1234",
            rol = "ARBITRO"
        ),
        User(
            id = 5,
            nombre = "María Belmonte",
            email = "maria5@gmail.com",
            password = "1234",
            rol = "JUGADOR"
        ),
        User(
            id = 6,
            nombre = "Juan López",
            email = "juan6@gmail.com",
            password = "1234",
            rol = "ENTRENADOR"
        ),
        User(
            id = 7,
            nombre = "Lucía Sánchez",
            email = "lucia7@gmail.com",
            password = "1234",
            rol = "JUGADOR"
        ),
        User(
            id = 8,
            nombre = "Carlos Martínez",
            email = "carlos8@gmail.com",
            password = "1234",
            rol = "ARBITRO"
        ),
        User(
            id = 9,
            nombre = "Laura Gómez",
            email = "laura9@gmail.com",
            password = "1234",
            rol = "JUGADOR"
        ),
        User(
            id = 10,
            nombre = "Sergio Fernández",
            email = "sergio10@gmail.com",
            password = "1234",
            rol = "ENTRENADOR"
        ),
        User(
            id = 11,
            nombre = "Elena Costa",
            email = "elena11@gmail.com",
            password = "1234",
            rol = "JUGADOR"
        ),
        User(
            id = 12,
            nombre = "David Ruiz",
            email = "david12@gmail.com",
            password = "1234",
            rol = "ARBITRO"
        ),
        User(
            id = 13,
            nombre = "Patricia Navarro",
            email = "patricia13@gmail.com",
            password = "1234",
            rol = "JUGADOR"
        ),
        User(
            id = 14,
            nombre = "Javier Ortega",
            email = "javier14@gmail.com",
            password = "1234",
            rol = "ENTRENADOR"
        ),
        User(
            id = 15,
            nombre = "Cristina Romero",
            email = "cristina15@gmail.com",
            password = "1234",
            rol = "JUGADOR"
        ),
        User(
            id = 16,
            nombre = "Miguel Herrera",
            email = "miguel16@gmail.com",
            password = "1234",
            rol = "ADMIN_DEPORTIVO"
        ),
        User(
            id = 17,
            nombre = "Sara Molina",
            email = "sara17@gmail.com",
            password = "1234",
            rol = "JUGADOR"
        ),
        User(
            id = 18,
            nombre = "Álvaro Domínguez",
            email = "alvaro18@gmail.com",
            password = "1234",
            rol = "ARBITRO"
        ),
        User(
            id = 19,
            nombre = "Noelia Pérez",
            email = "noelia19@gmail.com",
            password = "1234",
            rol = "JUGADOR"
        ),
        User(
            id = 20,
            nombre = "Hugo Vidal",
            email = "hugo20@gmail.com",
            password = "1234",
            rol = "ENTRENADOR"
        )
    )

    private fun getNewId(): Int {
        return (users.maxOfOrNull { it.id } ?: 0) + 1
    }

    override suspend fun addUser(user: User): User {
        val newId = getNewId()
        val newUser = user.copy(id = newId)
        users.add(newUser)
        return newUser
    }

    override suspend fun getUserById(id: Int): User? {
        return users.find { it.id == id }
    }

    override suspend fun updateUser(user: User): Boolean {
        val index = users.indexOfFirst { it.id == user.id }
        return if (index != -1) {
            users[index] = user
            true
        } else {
            false
        }
    }

    override suspend fun deleteUser(id: Int): Boolean {
        return users.removeIf { it.id == id }
    }

    override suspend fun getAllUsers(): List<User> {
        return users.toList()
    }

    override suspend fun getUsersByRole(rol: String): List<User> =
        users.filter { it.rol == rol }.toList()
}*/
