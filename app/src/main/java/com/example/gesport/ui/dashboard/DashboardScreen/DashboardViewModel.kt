package com.example.gesport.ui.dashboard.DashboardScreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {

    private val _userName = MutableLiveData("")
    val userName: LiveData<String> = _userName

    private val _navigateToGesUser = MutableLiveData(false)
    val navigateToGesUser: LiveData<Boolean> = _navigateToGesUser

    fun setUserName(name: String) {
        _userName.value = name
    }

    fun onGesUserCardClicked() {
        _navigateToGesUser.value = true
    }

    fun onNavigationToGesUserDone() {
        _navigateToGesUser.value = false
    }
}
