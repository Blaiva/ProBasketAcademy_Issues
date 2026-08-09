package com.probasketacademy.presentacion.categorias.detalle

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.probasketacademy.domain.model.Jugador
import com.probasketacademy.domain.repository.JugadorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriaDetalleViewModel @Inject constructor(
    private val jugadorRepository: JugadorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriaDetalleState())
    val uiState: StateFlow<CategoriaDetalleState> = _uiState.asStateFlow()

    fun onEvent(event: CategoriaDetalleEvent) {
        when (event) {
            is CategoriaDetalleEvent.OnRemoverJugador -> removerJugador(event.jugador)
        }
    }

    fun cargarJugadoresDeCategoria(categoriaId: Long) {
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

    private fun removerJugador(jugador: Jugador) {
        viewModelScope.launch {
            // Se asume que 0L significa sin categoría
            jugadorRepository.guardarJugador(jugador.copy(categoriaId = 0L, categoriaNombre = ""))
            cargarJugadoresDeCategoria(jugador.categoriaId) // Recargar
        }
    }
}