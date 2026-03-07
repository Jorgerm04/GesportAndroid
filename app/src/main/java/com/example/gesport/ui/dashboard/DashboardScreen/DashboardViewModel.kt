package com.example.gesport.ui.dashboard.DashboardScreen

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gesport.data.RoomUserRepository
import com.example.gesport.database.AppDatabase
import com.example.gesport.models.User
import kotlinx.coroutines.launch

class DashboardViewModel(private val userRepository: RoomUserRepository) : ViewModel() {

    private val _currentUser = MutableLiveData<User?>(null)
    val currentUser: LiveData<User?> = _currentUser

    private val _navigateToGesUser    = MutableLiveData(false)
    private val _navigateToGesCourt   = MutableLiveData(false)
    private val _navigateToGesTeam    = MutableLiveData(false)

    val navigateToGesUser:    LiveData<Boolean> = _navigateToGesUser
    val navigateToGesCourt:   LiveData<Boolean> = _navigateToGesCourt
    val navigateToGesTeam:    LiveData<Boolean> = _navigateToGesTeam

    fun loadUser(userId: Int) {
        viewModelScope.launch {
            _currentUser.value = userRepository.getUserById(userId)
        }
    }

    fun onGesUserCardClicked()  { _navigateToGesUser.value  = true }
    fun onGesCourtCardClicked() { _navigateToGesCourt.value = true }
    fun onGesTeamCardClicked()  { _navigateToGesTeam.value  = true }

    fun onNavigationDone() {
        _navigateToGesUser.value  = false
        _navigateToGesCourt.value = false
        _navigateToGesTeam.value  = false
    }
}

class DashboardViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val db   = AppDatabase.getDatabase(context)
        val repo = RoomUserRepository(db.userDao())
        return DashboardViewModel(repo) as T
    }
}