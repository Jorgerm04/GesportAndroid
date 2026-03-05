package com.example.gesport.ui.auth.RegisterScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gesport.data.RoomUserRepository
import com.example.gesport.database.AppDatabase

class RegisterViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db   = AppDatabase.getDatabase(context)
        val repo = RoomUserRepository(db.userDao())
        return RegisterViewModel(repo) as T
    }
}