package com.example.gesport.ui.dashboard.DashboardScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    private val _userName = MutableLiveData("")
    val userName: LiveData<String> = _userName

    private val _navigateToGesUser    = MutableLiveData(false)
    private val _navigateToGesCourt   = MutableLiveData(false)
    private val _navigateToGesTeam    = MutableLiveData(false)
    private val _navigateToGesBooking = MutableLiveData(false)

    val navigateToGesUser:    LiveData<Boolean> = _navigateToGesUser
    val navigateToGesCourt:   LiveData<Boolean> = _navigateToGesCourt
    val navigateToGesTeam:    LiveData<Boolean> = _navigateToGesTeam
    val navigateToGesBooking: LiveData<Boolean> = _navigateToGesBooking

    fun setUserName(name: String) {
        _userName.value = name
    }

    fun onGesUserCardClicked()    { _navigateToGesUser.value    = true }
    fun onGesCourtCardClicked()   { _navigateToGesCourt.value   = true }
    fun onGesTeamCardClicked()    { _navigateToGesTeam.value    = true }
    fun onGesBookingCardClicked() { _navigateToGesBooking.value = true }

    fun onNavigationDone() {
        _navigateToGesUser.value    = false
        _navigateToGesCourt.value   = false
        _navigateToGesTeam.value    = false
        _navigateToGesBooking.value = false
    }
}