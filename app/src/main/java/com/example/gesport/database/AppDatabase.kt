package com.example.gesport.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.gesport.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gessport_db"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Usamos una corrutina para insertar
                            CoroutineScope(Dispatchers.IO).launch {
                                // Obtenemos la instancia una vez creada
                                val dao = getDatabase(context).userDao()
                                populateDatabase(dao)
                            }
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateDatabase(userDao: UserDao) {
            val initialUsers = listOf(
                User(nombre = "Administrador", email = "admin@gesport.com", password = "123", rol = "ADMIN"),
                User(nombre = "Juan Pérez", email = "juan@test.com", password = "123", rol = "JUGADOR"),
                User(nombre = "María García", email = "maria@test.com", password = "123", rol = "JUGADOR"),
                User(nombre = "Carlos Ruiz", email = "carlos@test.com", password = "123", rol = "ENTRENADOR"),
                User(nombre = "Ana Martínez", email = "ana@test.com", password = "123", rol = "JUGADOR"),
                User(nombre = "Luis Fernández", email = "luis@test.com", password = "123", rol = "JUGADOR"),
                User(nombre = "Elena Blanco", email = "elena@test.com", password = "123", rol = "ENTRENADOR"),
                User(nombre = "Pablo Sanz", email = "pablo@test.com", password = "123", rol = "JUGADOR"),
                User(nombre = "Lucía Gómez", email = "lucia@test.com", password = "123", rol = "JUGADOR"),
                User(nombre = "Roberto Díaz", email = "roberto@test.com", password = "123", rol = "JUGADOR"),
                User(nombre = "Sonia Vega", email = "sonia@test.com", password = "123", rol = "JUGADOR")
            )

            initialUsers.forEach { user ->
                userDao.insert(user)
            }
        }
    }
}