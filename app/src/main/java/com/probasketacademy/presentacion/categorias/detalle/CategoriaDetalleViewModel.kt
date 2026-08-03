package com.probasketacademy.presentacion.categorias.detalle

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.probasketacademy.domain.repository.JugadorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriaDetalleViewModel @Inject constructor(
    private val jugadorRepository: JugadorRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val categoriaId: Long = checkNotNull(savedStateHandle["categoriaId"])

    private val _uiState = MutableStateFlow(CategoriaDetalleState())
    val uiState: StateFlow<CategoriaDetalleState> = _uiState.asStateFlow()

    init {
        cargarJugadoresDeCategoria()
    }

    fun onEvent(event: CategoriaDetalleEvent) {
        when (event) {
            is CategoriaDetalleEvent.OnRemoverJugador -> removerJugador(event.jugador)
        }
    }

    private fun cargarJugadoresDeCategoria() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            jugadorRepository.obtenerJugadores()
                .catch { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
                .collect { lista ->
                    // Filtramos solo los de esta categoría
                    val filtrados = lista.filter { it.categoriaId == categoriaId }
                    _uiState.update { it.copy(isLoading = false, jugadores = filtrados) }
                }
        }
    }

    private fun removerJugador(jugador: com.probasketacademy.domain.model.Jugador) {
        viewModelScope.launch {
            // Se asume que 0L significa sin categoría
            jugadorRepository.guardarJugador(jugador.copy(categoriaId = 0L, categoriaNombre = ""))
            cargarJugadoresDeCategoria() // Recargar
        }
    }
}