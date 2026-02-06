package com.example.gesport.ui.front.HomeScreen

import android.content.Context
import com.example.gesport.data.RoomUserRepository
import com.example.gesport.database.AppDatabase
import com.example.gesport.ui.dashboard.GesUserScreen.GesUserViewModel

class HomeViewModelFactory(private val appContext: Context) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        // Crear la base de datos (NECESITA Context)
        val database = AppDatabase.getDatabase(appContext)

        // Obtener el DAO
        val userDao = database.userDao()

        val repo = RoomUserRepository(userDao)  // se crea tu repo real
        return HomeViewModel(repo) as T
    }
}