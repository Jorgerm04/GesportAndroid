package com.example.gesport.ui.dashboard.GesBookingsScreen

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.gesport.data.RoomBookingRepository
import com.example.gesport.data.RoomCourtRepository
import com.example.gesport.data.RoomTeamRepository
import com.example.gesport.data.RoomUserRepository
import com.example.gesport.database.AppDatabase

class GesBookingViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db          = AppDatabase.getDatabase(context)
        val bookingRepo = RoomBookingRepository(db.bookingDao())
        val courtRepo   = RoomCourtRepository(db.courtDao())
        val userRepo    = RoomUserRepository(db.userDao())
        val teamRepo    = RoomTeamRepository(db.teamDao())
        return GesBookingViewModel(bookingRepo, courtRepo, userRepo,teamRepo) as T
    }
}