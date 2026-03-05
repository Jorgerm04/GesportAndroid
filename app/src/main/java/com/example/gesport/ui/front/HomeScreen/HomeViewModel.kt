package com.example.gesport.ui.front.HomeScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gesport.models.Booking
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

    fun loadUser(userName: String) {
        viewModelScope.launch {
            userRepository.getAllUsers().collect { lista ->
                _user.value = lista.find { it.nombre == userName }
            }
        }
    }

    fun loadBookings(userId: Int, rol: String) {
        viewModelScope.launch {
            // Combinamos los dos flows: cuando cambia cualquiera de los dos
            // se recalcula el filtro automáticamente
            combine(
                teamRepository.getAllTeams(),
                bookingRepository.getAllBookings()
            ) { teams, allBookings ->

                // IDs de equipos a los que pertenece el usuario
                val equiposDelUsuario: List<Int> = teams
                    .filter { team ->
                        when (rol) {
                            "JUGADOR"    -> team.getJugadoresIdsList().contains(userId)
                            "ENTRENADOR" -> team.entrenadorId == userId
                            else         -> false
                        }
                    }
                    .map { it.id }

                // Filtramos reservas según rol
                allBookings.filter { b ->
                    if (b.cancelada) return@filter false
                    when (rol) {
                        "JUGADOR", "ENTRENADOR" -> {
                            val esIndividual = b.usuarioId == userId
                            val esDeEquipo   = b.equipoId != null &&
                                    equiposDelUsuario.contains(b.equipoId)
                            val esPartido    = equiposDelUsuario.contains(b.equipoLocalId) ||
                                    equiposDelUsuario.contains(b.equipoVisitanteId)
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