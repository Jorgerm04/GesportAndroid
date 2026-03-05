package com.example.gesport.ui.dashboard.GesTeamsScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gesport.data.RoomTeamRepository
import com.example.gesport.data.RoomUserRepository
import com.example.gesport.database.AppDatabase

class GesTeamViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db       = AppDatabase.getDatabase(context)
        val teamRepo = RoomTeamRepository(db.teamDao())
        val userRepo = RoomUserRepository(db.userDao())
        return GesTeamViewModel(teamRepo, userRepo) as T
    }
}