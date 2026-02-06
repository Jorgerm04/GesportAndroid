package com.example.gesport.ui.auth.LoginScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gesport.data.RoomUserRepository
import com.example.gesport.database.AppDatabase

class LoginViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val database = AppDatabase.getDatabase(context)
        val repo = RoomUserRepository(database.userDao())
        return LoginViewModel(repo) as T
    }
}