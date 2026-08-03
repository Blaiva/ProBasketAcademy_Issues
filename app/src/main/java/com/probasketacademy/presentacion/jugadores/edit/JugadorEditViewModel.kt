package com.probasketacademy.presentacion.jugadores.edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.probasketacademy.domain.repository.JugadorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JugadorEditViewModel @Inject constructor(
    private val jugadorRepository: JugadorRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(JugadorEditState())
    val uiState: StateFlow<JugadorEditState> = _uiState.asStateFlow()

    init {
        val jugadorId: Long? = savedStateHandle.get<Long>("jugadorId")
        jugadorId?.let { cargarJugador(it) }
    }

    fun onEvent(event: JugadorEditEvent) {
        when (event) {
            is JugadorEditEvent.OnGuardarClicked -> guardarJugador()
        }
    }

    private fun cargarJugador(id: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            jugadorRepository.obtenerJugadorPorId(id)
                .catch { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
                .collect { jugador ->
                    _uiState.update { it.copy(isLoading = false, jugador = jugador) }
                }
        }
    }

    private fun guardarJugador() {
        viewModelScope.launch {
            val jugadorActual = _uiState.value.jugador ?: return@launch

            _uiState.update { it.copy(isLoading = true) }
            jugadorRepository.guardarJugador(jugadorActual) // Usa el repositorio real para guardar[cite: 4]

            _uiState.update { it.copy(isLoading = false, isSaved = true) }
        }
    }
}