package com.example.gesport.ui.dashboard.GesBookingsScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gesport.models.Booking
import com.example.gesport.models.Court
import com.example.gesport.models.Team
import com.example.gesport.models.User
import com.example.gesport.repository.BookingRepository
import com.example.gesport.repository.CourtRepository
import com.example.gesport.repository.TeamRepository
import com.example.gesport.repository.UserRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class GesBookingViewModel(
    private val bookingRepository: BookingRepository,
    private val courtRepository: CourtRepository,
    private val userRepository: UserRepository,
    private val teamRepository: TeamRepository
) : ViewModel() {

    private var _bookings by mutableStateOf<List<Booking>>(emptyList())
    val bookings: List<Booking> get() = _bookings

    private var _showCancelled by mutableStateOf(true)
    val showCancelled: Boolean get() = _showCancelled

    private var _allUsers by mutableStateOf<List<User>>(emptyList())
    val allUsers: List<User> get() = _allUsers

    private var _allCourts by mutableStateOf<List<Court>>(emptyList())
    val allCourts: List<Court> get() = _allCourts

    private var _allTeams by mutableStateOf<List<Team>>(emptyList())
    val allTeams: List<Team> get() = _allTeams

    private val _currentBooking  = MutableLiveData<Booking?>(null)
    val currentBooking: LiveData<Booking?> = _currentBooking

    private val _saveCompleted   = MutableLiveData(false)
    val saveCompleted: LiveData<Boolean> = _saveCompleted

    private val _conflictError   = MutableLiveData<String?>(null)
    val conflictError: LiveData<String?> = _conflictError

    init {
        viewModelScope.launch { bookingRepository.getAllBookings().collect { _bookings = applyFilter(it) } }
        viewModelScope.launch { userRepository.getAllUsers().collect   { _allUsers  = it } }
        viewModelScope.launch { courtRepository.getAllCourts().collect { _allCourts = it } }
        viewModelScope.launch { teamRepository.getAllTeams().collect   { _allTeams  = it } }
    }

    fun onShowCancelledChange(value: Boolean) {
        _showCancelled = value
        viewModelScope.launch { refreshBookings() }
    }

    private suspend fun refreshBookings() {
        _bookings = applyFilter(bookingRepository.getAllBookings().first())
    }

    private fun applyFilter(base: List<Booking>) =
        if (_showCancelled) base else base.filter { !it.cancelada }

    fun loadBookingById(id: Int) {
        viewModelScope.launch { _currentBooking.value = bookingRepository.getBookingById(id) }
    }

    suspend fun getBookingsForCourtAndDay(courtId: Int, dayStart: Long, dayEnd: Long): List<Booking> {
        return bookingRepository.getAllBookings().first().filter { b ->
            b.pistaId == courtId && b.horaInicio >= dayStart && b.horaInicio < dayEnd
        }
    }

    fun saveBooking(
        bookingId: Int?,
        tipo: String,
        usuarioId: Int? = null,
        usuarioNombre: String? = null,
        pistaId: Int,
        pistaNombre: String,
        fecha: Long,
        horaInicio: Long,
        horaFin: Long,
        notas: String? = null,
        equipoId: Int? = null,
        equipoNombre: String? = null,
        equipoLocalId: Int? = null,
        equipoLocalNombre: String? = null,
        equipoVisitanteId: Int? = null,
        equipoVisitanteNombre: String? = null,
        arbitroId: Int? = null,
        arbitroNombre: String? = null,
        puntosLocal: Int? = null,
        puntosVisitante: Int? = null
    ) {
        viewModelScope.launch {
            val conflict = bookingRepository.hasConflict(pistaId, horaInicio, horaFin, bookingId)
            if (conflict) { _conflictError.value = "La pista ya está reservada en ese horario."; return@launch }

            val booking = Booking(
                id                    = bookingId ?: 0,
                tipo                  = tipo,
                usuarioId             = usuarioId,
                usuarioNombre         = usuarioNombre,
                pistaId               = pistaId,
                pistaNombre           = pistaNombre,
                fecha                 = fecha,
                horaInicio            = horaInicio,
                horaFin               = horaFin,
                notas                 = notas,
                equipoId              = equipoId,
                equipoNombre          = equipoNombre,
                equipoLocalId         = equipoLocalId,
                equipoLocalNombre     = equipoLocalNombre,
                equipoVisitanteId     = equipoVisitanteId,
                equipoVisitanteNombre = equipoVisitanteNombre,
                arbitroId             = arbitroId,
                arbitroNombre         = arbitroNombre,
                puntosLocal           = puntosLocal,
                puntosVisitante       = puntosVisitante
            )

            if (bookingId == null) bookingRepository.addBooking(booking)
            else bookingRepository.updateBooking(booking)

            refreshBookings()
            _saveCompleted.value = true
        }
    }

    fun setCancelada(id: Int, cancelada: Boolean) {
        viewModelScope.launch { bookingRepository.setCancelada(id, cancelada); refreshBookings() }
    }

    fun deleteBooking(id: Int) {
        viewModelScope.launch { bookingRepository.deleteBooking(id); refreshBookings() }
    }

    fun onSaveCompletedHandled() { _saveCompleted.value = false }
    fun onConflictErrorHandled() { _conflictError.value = null }
}