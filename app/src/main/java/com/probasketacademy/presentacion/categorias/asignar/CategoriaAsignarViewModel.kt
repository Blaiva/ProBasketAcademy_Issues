package com.probasketacademy.presentacion.categorias.asignar

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.probasketacademy.domain.repository.JugadorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriaAsignarViewModel @Inject constructor(
    private val jugadorRepository: JugadorRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Obtenemos el ID de la categoría desde la navegación
    private val categoriaId: Long = checkNotNull(savedStateHandle["categoriaId"])

    private val _uiState = MutableStateFlow(CategoriaAsignarState())
    val uiState: StateFlow<CategoriaAsignarState> = _uiState.asStateFlow()

    init {
        cargarJugadores()
    }

    fun onEvent(event: CategoriaAsignarEvent) {
        when (event) {
            is CategoriaAsignarEvent.OnSearchQueryChanged -> {
                _uiState.update { it.copy(searchQuery = event.query) }
                if (event.query.isBlank()) cargarJugadores() else buscarJugadores(event.query)
            }
            is CategoriaAsignarEvent.OnJugadorToggled -> {
                val actuales = _uiState.value.seleccionados.toMutableSet()
                if (event.isSelected) actuales.add(event.jugadorId) else actuales.remove(event.jugadorId)
                _uiState.update { it.copy(seleccionados = actuales) }
            }
            is CategoriaAsignarEvent.OnGuardarAsignacion -> guardarAsignaciones()
        }
    }

    private fun cargarJugadores() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Cargamos todos los jugadores para poder asignarlos
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
                .collect { lista -> _uiState.update { it.copy(jugadores = lista) } }
        }
    }

    private fun guardarAsignaciones() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val state = _uiState.value

            // Filtramos los jugadores seleccionados y les actualizamos su categoriaId
            val jugadoresAActualizar = state.jugadores.filter { state.seleccionados.contains(it.jugadorId) }

            jugadoresAActualizar.forEach { jugador ->
                jugadorRepository.guardarJugador(jugador.copy(categoriaId = categoriaId))
            }

            _uiState.update { it.copy(isLoading = false, isSaved = true) }
        }
    }
}