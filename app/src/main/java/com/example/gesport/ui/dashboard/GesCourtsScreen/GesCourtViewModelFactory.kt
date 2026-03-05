package com.example.gesport.ui.dashboard.GesCourtsScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gesport.data.RoomCourtRepository
import com.example.gesport.database.AppDatabase

class GesCourtViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val dao  = AppDatabase.getDatabase(context).courtDao()
        val repo = RoomCourtRepository(dao)
        return GesCourtViewModel(repo) as T
    }
}