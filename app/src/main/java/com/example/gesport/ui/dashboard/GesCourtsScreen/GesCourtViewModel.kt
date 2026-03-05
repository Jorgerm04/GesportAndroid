package com.example.gesport.ui.dashboard.GesCourtsScreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gesport.models.Court
import com.example.gesport.models.CourtType
import com.example.gesport.repository.CourtRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class GesCourtViewModel(private val courtRepository: CourtRepository) : ViewModel() {

    private var _courts by mutableStateOf<List<Court>>(emptyList())
    val courts: List<Court> get() = _courts

    private var _searchQuery by mutableStateOf("")
    val searchQuery: String get() = _searchQuery

    private var _showOnlyActive by mutableStateOf(false)
    val showOnlyActive: Boolean get() = _showOnlyActive

    private val _currentCourt = MutableLiveData<Court?>(null)
    val currentCourt: LiveData<Court?> = _currentCourt

    private val _saveCompleted = MutableLiveData(false)
    val saveCompleted: LiveData<Boolean> = _saveCompleted

    init {
        viewModelScope.launch {
            courtRepository.getAllCourts().collect { lista ->
                _courts = applyFilters(lista)
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery = query
        viewModelScope.launch { refreshCourts() }
    }

    fun onShowOnlyActiveChange(value: Boolean) {
        _showOnlyActive = value
        viewModelScope.launch { refreshCourts() }
    }

    private suspend fun refreshCourts() {
        val base = courtRepository.getAllCourts().first()
        _courts = applyFilters(base)
    }

    private fun applyFilters(base: List<Court>): List<Court> {
        var result = base
        if (_showOnlyActive) result = result.filter { it.activa }
        val q = _searchQuery.trim().lowercase()
        if (q.isNotBlank()) {
            result = result.filter {
                it.nombre.lowercase().contains(q) ||
                        it.tipoEnum.label.lowercase().contains(q) ||
                        it.descripcion.lowercase().contains(q)
            }
        }
        return result
    }

    fun loadCourtById(id: Int) {
        viewModelScope.launch {
            _currentCourt.value = courtRepository.getCourtById(id)
        }
    }

    fun saveCourt(
        courtId: Int?,
        nombre: String,
        tipo: CourtType,
        descripcion: String,
        activa: Boolean,
        precioPorHora: Double
    ) {
        viewModelScope.launch {
            if (courtId == null) {
                courtRepository.addCourt(
                    Court(
                        nombre = nombre,
                        tipo = tipo.name,
                        descripcion = descripcion,
                        activa = activa,
                        precioPorHora = precioPorHora
                    )
                )
            } else {
                val existing = courtRepository.getCourtById(courtId)
                if (existing != null) {
                    courtRepository.updateCourt(
                        existing.copy(
                            nombre = nombre,
                            tipo = tipo.name,
                            descripcion = descripcion,
                            activa = activa,
                            precioPorHora = precioPorHora
                        )
                    )
                }
            }
            refreshCourts()
            _saveCompleted.value = true
        }
    }

    fun deleteCourt(id: Int) {
        viewModelScope.launch {
            courtRepository.deleteCourt(id)
            refreshCourts()
        }
    }

    fun onSaveCompletedHandled() {
        _saveCompleted.value = false
    }
}