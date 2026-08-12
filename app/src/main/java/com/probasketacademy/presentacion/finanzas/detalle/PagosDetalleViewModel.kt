package com.probasketacademy.presentacion.finanzas.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.probasketacademy.domain.model.Pago
import com.probasketacademy.domain.repository.JugadorRepository
import com.probasketacademy.domain.repository.PagoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PagosDetalleViewModel @Inject constructor(
    private val jugadorRepository: JugadorRepository,
    private val pagoRepository: PagoRepository
) : ViewModel() {

    private var jugadorId: Long = 0

    private val _uiState = MutableStateFlow(PagosDetalleState())
    val uiState: StateFlow<PagosDetalleState> = _uiState.asStateFlow()

    fun onEvent(event: PagosDetalleEvent) {
        when (event) {
            is PagosDetalleEvent.OnTogglePagoDialog -> {
                _uiState.update { it.copy(showPagoDialog = !it.showPagoDialog) }
            }
            is PagosDetalleEvent.OnRegistrarPago -> {
                registrarNuevoPago(event.montoS, event.esMensual)
            }
            is PagosDetalleEvent.OnMarcarComoPagado -> {
                marcarComoPagado(event.pago)
            }
        }
    }

    fun cargarDatos(id: Long) {
        this.jugadorId = id
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            jugadorRepository.obtenerJugadorPorId(jugadorId).collectLatest { jugador ->
                _uiState.update { it.copy(jugador = jugador) }
                if (jugador != null) {
                    cargarPagos()
                }
            }
        }
    }

    private fun cargarPagos() {
        viewModelScope.launch {
            pagoRepository.obtenerPagosPorJugador(jugadorId)
                .catch { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
                .collect { listaPagos ->
                    val saldo = listaPagos.filter { it.estado == "PENDIENTE" }.sumOf { it.monto }

                    // Ordenamos para que los PENDIENTES queden arriba siempre
                    val pagosOrdenados = listaPagos.sortedByDescending { it.estado == "PENDIENTE" }

                    _uiState.update {
                        it.copy(isLoading = false, pagos = pagosOrdenados, saldoPendiente = saldo)
                    }
                }
        }
    }

    private fun registrarNuevoPago(montoString: String, esMensual: Boolean) {
        viewModelScope.launch {
            val montoReal = montoString.toDoubleOrNull() ?: 0.0
            if (montoReal <= 0) return@launch // Validación rápida

            _uiState.update { it.copy(isLoading = true, showPagoDialog = false) }

            val sdf = SimpleDateFormat("dd MMM yyyy", Locale("es", "ES"))
            val fechaActual = sdf.format(Date())

            val conceptoGenerado = if (esMensual) {
                "Cuota Mensual - Inicia $fechaActual"
            } else {
                "Cuota Semanal - Inicia $fechaActual"
            }

            val nuevoPago = Pago(
                jugadorId = jugadorId,
                concepto = conceptoGenerado,
                monto = montoReal,
                fecha = fechaActual,
                estado = "PENDIENTE", // <--- EL CAMBIO CLAVE ESTÁ AQUÍ (Antes decía "PAGADO")
                jugadorNombre = _uiState.value.jugador?.nombre ?: "",
                numeroCamiseta = _uiState.value.jugador?.numeroCamiseta ?: 0
            )

            pagoRepository.registrarPago(nuevoPago)
            cargarPagos() // Refrescamos la lista
        }
    }

    private fun marcarComoPagado(pago: Pago) {
        viewModelScope.launch {
            // Creamos una copia del pago exacto pero con estado "PAGADO"
            val pagoActualizado = pago.copy(estado = "PAGADO")

            // Lo guardamos en la base de datos (se actualiza automáticamente)
            pagoRepository.registrarPago(pagoActualizado)

            cargarPagos() // Refrescamos para recalcular el saldo
        }
    }
}