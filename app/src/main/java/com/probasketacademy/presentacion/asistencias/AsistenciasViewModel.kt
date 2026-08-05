package com.probasketacademy.presentacion.asistencias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.probasketacademy.domain.model.Asistencia
import com.probasketacademy.domain.repository.AsistenciaRepository
import com.probasketacademy.domain.repository.JugadorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AsistenciasViewModel @Inject constructor(
    private val jugadorRepository: JugadorRepository,
    private val asistenciaRepository: AsistenciaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AsistenciasState())
    val uiState: StateFlow<AsistenciasState> = _uiState.asStateFlow()

    init {
        cargarJugadores()
    }

    fun onEvent(event: AsistenciasEvent) {
        when (event) {
            is AsistenciasEvent.OnJugadorToggled -> {
                val nuevasAsistencias = _uiState.value.asistencias.toMutableMap()
                nuevasAsistencias[event.jugadorId] = event.asistio
                _uiState.update { it.copy(asistencias = nuevasAsistencias) }
            }
            is AsistenciasEvent.OnConfirmarAsistencia -> guardarAsistencias()
        }
    }

    private fun cargarJugadores() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Cargamos la lista de jugadores que ya tenemos creados[cite: 1]
            jugadorRepository.obtenerJugadores()
                .catch { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
                .collect { lista ->
                    // Inicializamos todos como "No asistió" (false) por defecto
                    val asistenciasIniciales = lista.associate { it.jugadorId to false }
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            jugadores = lista,
                            asistencias = asistenciasIniciales
                        )
                    }
                }
        }
    }

    private fun guardarAsistencias() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val state = _uiState.value
            val fechaActualMs = System.currentTimeMillis()

            // Mapeamos los datos al modelo de dominio Asistencia
            val registros = state.jugadores.map { jugador ->
                Asistencia(
                    jugadorId = jugador.jugadorId,
                    categoriaId = jugador.categoriaId,
                    fechaEpocaMs = fechaActualMs,
                    asistio = state.asistencias[jugador.jugadorId] ?: false,
                    nombreJugador = jugador.nombre
                )
            }

            // Guardamos usando el repositorio[cite: 1]
            asistenciaRepository.registrarAsistencias(registros)
            _uiState.update { it.copy(isLoading = false, isSaved = true) }
        }
    }
}