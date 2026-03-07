package com.example.gesport.ui.front.HomeScreen

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.gesport.data.RoomBookingRepository
import com.example.gesport.data.RoomTeamRepository
import com.example.gesport.data.RoomUserRepository
import com.example.gesport.database.AppDatabase
import com.example.gesport.models.Booking
import com.example.gesport.models.Team
import com.example.gesport.models.User
import com.example.gesport.repository.BookingRepository
import com.example.gesport.repository.TeamRepository
import com.example.gesport.repository.UserRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeViewModel(
    private val userRepository: UserRepository,
    private val bookingRepository: BookingRepository,
    private val teamRepository: TeamRepository
) : ViewModel() {

    private val _user = MutableLiveData<User?>(null)
    val user: LiveData<User?> = _user

    var bookings by mutableStateOf<List<Booking>>(emptyList())
        private set

    var misEquipos by mutableStateOf<List<Team>>(emptyList())
        private set

    // Punto de entrada único: recibe el userId y lo carga todo
    fun init(userId: Int) {
        viewModelScope.launch {
            userRepository.getAllUsers().collect { lista ->
                val user = lista.find { it.id == userId }
                _user.value = user
                if (user != null) loadBookings(user.id, user.rol)
            }
        }
    }

    private fun loadBookings(userId: Int, rol: String) {
        viewModelScope.launch {
            combine(
                teamRepository.getAllTeams(),
                bookingRepository.getAllBookings()
            ) { teams, allBookings ->

                val equiposDelUsuario = teams.filter { team ->
                    when (rol) {
                        "JUGADOR"    -> team.getJugadoresIdsList().contains(userId)
                        "ENTRENADOR" -> team.entrenadorId == userId
                        else         -> false
                    }
                }
                misEquipos = equiposDelUsuario

                val equiposIds = equiposDelUsuario.map { it.id }

                allBookings.filter { b ->
                    if (b.cancelada) return@filter false
                    when (rol) {
                        "JUGADOR", "ENTRENADOR" -> {
                            val esIndividual = b.usuarioId == userId
                            val esDeEquipo   = b.equipoId != null && equiposIds.contains(b.equipoId)
                            val esPartido    = equiposIds.contains(b.equipoLocalId) ||
                                    equiposIds.contains(b.equipoVisitanteId)
                            esIndividual || esDeEquipo || esPartido
                        }
                        "ARBITRO" -> b.arbitroId == userId
                        else      -> false
                    }
                }.sortedBy { it.horaInicio }

            }.collect { filtradas ->
                bookings = filtradas
            }
        }
    }

    fun logout(onLogout: () -> Unit) { onLogout() }
}