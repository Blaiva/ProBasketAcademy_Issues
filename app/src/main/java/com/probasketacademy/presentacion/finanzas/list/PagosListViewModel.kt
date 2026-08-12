package com.probasketacademy.presentacion.finanzas.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.probasketacademy.domain.repository.JugadorRepository
import com.probasketacademy.presentacion.finanzas.list.PagosListEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PagosListViewModel @Inject constructor(
    private val jugadorRepository: JugadorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PagosListState())
    val uiState: StateFlow<PagosListState> = _uiState.asStateFlow()

    init {
        cargarJugadores()
    }

    fun onEvent(event: PagosListEvent) {
        when (event) {
            is PagosListEvent.OnSearchQueryChanged -> {
                _uiState.update { it.copy(searchQuery = event.query) }
                if (event.query.isBlank()) cargarJugadores() else buscarJugadores(event.query)
            }
        }
    }

    private fun cargarJugadores() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            jugadorRepository.obtenerJugadores() // Usamos el repositorio existente
                .catch { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
                .collect { lista -> _uiState.update { it.copy(isLoading = false, jugadores = lista) } }
        }
    }

    private fun buscarJugadores(query: String) {
        viewModelScope.launch {
            jugadorRepository.buscarJugadores(query) // Búsqueda nativa desde tu Room DB[cite: 8]
                .catch { e -> _uiState.update { it.copy(errorMessage = e.message) } }
                .collect { lista -> _uiState.update { it.copy(jugadores = lista) } }
        }
    }
}