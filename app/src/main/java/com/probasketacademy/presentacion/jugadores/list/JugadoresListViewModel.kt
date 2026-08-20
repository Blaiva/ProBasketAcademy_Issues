package com.probasketacademy.presentacion.jugadores.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.probasketacademy.domain.repository.JugadorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JugadoresListViewModel @Inject constructor(
    private val jugadorRepository: JugadorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(JugadoresListState())
    val uiState: StateFlow<JugadoresListState> = _uiState.asStateFlow()

    init {
        cargarJugadores()
    }

    fun onEvent(event: JugadoresListEvent) {
        when (event) {
            is JugadoresListEvent.OnSearchQueryChanged -> {
                _uiState.update { it.copy(searchQuery = event.query) }
                if (event.query.isBlank()) {
                    cargarJugadores()
                } else {
                    buscarJugadores(event.query)
                }
            }
            is JugadoresListEvent.OnAddJugadorClicked -> Unit
            is JugadoresListEvent.OnJugadorClicked -> Unit
        }
    }

    private fun cargarJugadores() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            jugadorRepository.obtenerJugadores()
                .catch { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
                .collect { lista ->
                    _uiState.update { it.copy(isLoading = false, jugadores = lista) }
                }
        }
    }

    private fun buscarJugadores(query: String) {
        viewModelScope.launch {
            jugadorRepository.buscarJugadores(query)
                .catch { e -> _uiState.update { it.copy(errorMessage = e.message) } }
                .collect { lista ->
                    _uiState.update { it.copy(jugadores = lista) }
                }
        }
    }
}