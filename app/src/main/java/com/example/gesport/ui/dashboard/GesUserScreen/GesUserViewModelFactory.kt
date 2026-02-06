package com.example.gesport.ui.dashboard.GesUserScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gesport.data.RoomUserRepository
import com.example.gesport.database.AppDatabase

class GesUserViewModelFactory(
    private val appContext: Context
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        val database = AppDatabase.getDatabase(appContext)

        // Obtener el DAO
        val userDao = database.userDao()

        val repo = RoomUserRepository(userDao)  // se crea tu repo real
        return GesUserViewModel(repo) as T  //se crea el ViewModel
    }
}