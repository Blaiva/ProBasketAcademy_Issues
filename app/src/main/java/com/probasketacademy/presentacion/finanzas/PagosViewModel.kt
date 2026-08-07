package com.probasketacademy.presentacion.finanzas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.probasketacademy.domain.model.Jugador
import com.probasketacademy.domain.model.Pago
import com.probasketacademy.domain.repository.JugadorRepository
import com.probasketacademy.domain.repository.PagoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PagosViewModel @Inject constructor(
    private val jugadorRepository: JugadorRepository,
    private val pagoRepository: PagoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PagosState())
    val uiState: StateFlow<PagosState> = _uiState.asStateFlow()

    init {
        cargarDatos()
    }

    fun onEvent(event: PagosEvent) {
        when (event) {
            is PagosEvent.OnEnviarRecordatorio -> { /* Lógica para enviar recordatorio */ }
            is PagosEvent.OnRegistrarPago -> { /* Lógica para abrir modal de pago */ }
        }
    }

    private fun cargarDatos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Obtenemos el primer jugador activo para mostrar el estado de cuenta por defecto
            jugadorRepository.obtenerJugadores().collect { jugadores ->
                val primerJugador = jugadores.firstOrNull() ?: Jugador(
                    nombre = "JUAN PÉREZ",
                    numeroCamiseta = 12,
                    categoriaNombre = "U-16"
                )

                _uiState.update { it.copy(jugador = primerJugador) }
                cargarPagos(primerJugador.jugadorId)
            }
        }
    }

    private fun cargarPagos(jugadorId: Long) {
        viewModelScope.launch {
            pagoRepository.obtenerPagosPorJugador(jugadorId)
                .catch { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
                .collect { listaPagos ->
                    // Si no hay pagos, cargamos datos de prueba para igualar el diseño
                    val pagosAMostrar = if (listaPagos.isEmpty()) getDummyPagos() else listaPagos
                    val saldo = pagosAMostrar.filter { it.estado == "PENDIENTE" }.sumOf { it.monto }

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            pagos = pagosAMostrar,
                            saldoPendiente = saldo
                        )
                    }
                }
        }
    }

    private fun getDummyPagos(): List<Pago> {
        return listOf(
            Pago(id = 1, concepto = "Cuota Mensual Jul", monto = 80.0, fecha = "15 Jul 2024", estado = "PENDIENTE"),
            Pago(id = 2, concepto = "Pago Cuota Jun", monto = 40.0, fecha = "16 Jul 2024", estado = "PAGADO"),
            Pago(id = 3, concepto = "Pago Cuota May", monto = 0.0, fecha = "13 Jul 2024", estado = "PAGADO"),
            Pago(id = 4, concepto = "Cuota Mensual Mar", monto = 0.0, fecha = "17 Jul 2024", estado = "PAGADO")
        )
    }
}