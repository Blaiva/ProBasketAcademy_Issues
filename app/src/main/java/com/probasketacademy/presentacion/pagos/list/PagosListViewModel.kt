package com.probasketacademy.presentacion.pagos.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.probasketacademy.domain.repository.JugadorRepository
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
            jugadorRepository.obtenerJugadores()
                .catch { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
                .collect { lista ->
                    val generadoGlobal = lista.sumOf { it.totalGenerado }
                    val pagadoGlobal = lista.sumOf { it.totalPagado }
                    val deudaGlobal = lista.sumOf { it.deudaActual }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            jugadores = lista,
                            totalGeneradoGlobal = generadoGlobal,
                            totalPagadoGlobal = pagadoGlobal,
                            deudaGlobal = deudaGlobal
                        )
                    }
                }
        }
    }

    private fun buscarJugadores(query: String) {
        viewModelScope.launch {
            jugadorRepository.buscarJugadores(query)
                .catch { e -> _uiState.update { it.copy(errorMessage = e.message) } }
                .collect { lista ->
                    val generadoGlobal = lista.sumOf { it.totalGenerado }
                    val pagadoGlobal = lista.sumOf { it.totalPagado }
                    val deudaGlobal = lista.sumOf { it.deudaActual }

                    _uiState.update {
                        it.copy(
                            jugadores = lista,
                            totalGeneradoGlobal = generadoGlobal,
                            totalPagadoGlobal = pagadoGlobal,
                            deudaGlobal = deudaGlobal
                        )
                    }
                }
        }
    }
}