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
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class PagosDetalleViewModel @Inject constructor(
    private val jugadorRepository: JugadorRepository,
    private val pagoRepository: PagoRepository
) : ViewModel() {

    private var jugadorId: Long = 0
    private val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    private val _uiState = MutableStateFlow(PagosDetalleState())
    val uiState: StateFlow<PagosDetalleState> = _uiState.asStateFlow()

    init {
        _uiState.update { it.copy(fechaInicio = sdf.format(Date())) }
        calcularVencimiento()
    }

    fun onEvent(event: PagosDetalleEvent) {
        when (event) {
            is PagosDetalleEvent.OnTogglePagoDialog -> {
                _uiState.update { it.copy(showPagoDialog = !it.showPagoDialog) }
            }
            is PagosDetalleEvent.OnToggleAbonoDialog -> {
                _uiState.update { it.copy(
                    showAbonoDialog = !it.showAbonoDialog,
                    selectedPagoParaAbono = event.pago,
                    montoNuevoAbonoInput = ""
                ) }
            }
            is PagosDetalleEvent.OnConceptoChanged -> {
                _uiState.update { it.copy(conceptoInput = event.concepto) }
            }
            is PagosDetalleEvent.OnMontoTotalChanged -> {
                _uiState.update { it.copy(montoTotalInput = event.monto) }
            }
            is PagosDetalleEvent.OnMontoAbonadoChanged -> {
                _uiState.update { it.copy(montoAbonadoInput = event.monto) }
            }
            is PagosDetalleEvent.OnMontoNuevoAbonoChanged -> {
                _uiState.update { it.copy(montoNuevoAbonoInput = event.monto) }
            }
            is PagosDetalleEvent.OnTipoInscripcionChanged -> {
                _uiState.update { it.copy(tipoInscripcion = event.value) }
                calcularVencimiento()
            }
            is PagosDetalleEvent.OnFechaInicioChanged -> {
                _uiState.update { it.copy(fechaInicio = event.value) }
                calcularVencimiento()
            }
            is PagosDetalleEvent.OnRegistrarPago -> {
                registrarNuevoPago()
            }
            is PagosDetalleEvent.OnRegistrarAbono -> {
                registrarAbono()
            }
            is PagosDetalleEvent.OnMarcarComoPagado -> {
                marcarComoPagado(event.pago)
            }
            is PagosDetalleEvent.OnToggleSaldarConfirmDialog -> {
                _uiState.update {
                    it.copy(
                        showSaldarConfirmDialog = !it.showSaldarConfirmDialog,
                        pagoParaSaldar = event.pago
                    )
                }
            }
            is PagosDetalleEvent.OnConfirmarSaldar -> {
                _uiState.value.pagoParaSaldar?.let { marcarComoPagado(it) }
                _uiState.update { it.copy(showSaldarConfirmDialog = false, pagoParaSaldar = null) }
            }
        }
    }

    private fun calcularVencimiento() {
        val state = _uiState.value
        try {
            val fechaInicio = sdf.parse(state.fechaInicio) ?: Date()
            val calendar = Calendar.getInstance()
            calendar.time = fechaInicio

            if (state.tipoInscripcion == "Semanal") {
                calendar.add(Calendar.DAY_OF_YEAR, 7)
            } else {
                calendar.add(Calendar.MONTH, 1)
            }

            _uiState.update { it.copy(fechaVencimiento = sdf.format(calendar.time)) }
        } catch (e: Exception) {
            // Error al parsear fecha, ignorar
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
                    val saldo = listaPagos.sumOf { it.deuda }
                    val pagosOrdenados = listaPagos.sortedByDescending { it.estado != "PAGADO" }
                    _uiState.update {
                        it.copy(isLoading = false, pagos = pagosOrdenados, saldoPendiente = saldo)
                    }
                }
        }
    }

    private fun registrarNuevoPago() {
        viewModelScope.launch {
            val state = _uiState.value
            val montoTotal = state.montoTotalInput.toDoubleOrNull() ?: 0.0
            val montoAbonado = state.montoAbonadoInput.toDoubleOrNull() ?: 0.0
            val concepto = state.conceptoInput.ifBlank { "Cuota Academia" }

            if (montoTotal <= 0) return@launch

            val deuda = (montoTotal - montoAbonado).coerceAtLeast(0.0)
            val estado = when {
                deuda <= 0.0 -> "PAGADO"
                montoAbonado > 0.0 -> "ABONADO"
                else -> "PENDIENTE"
            }

            _uiState.update { it.copy(isLoading = true, showPagoDialog = false) }

            val sdfPago = SimpleDateFormat("dd MMM yyyy", Locale("es", "ES"))
            val fechaActual = sdfPago.format(Date())

            val nuevoPago = Pago(
                jugadorId = jugadorId,
                concepto = concepto,
                montoTotal = montoTotal,
                montoPagado = montoAbonado,
                deuda = deuda,
                fecha = fechaActual,
                estado = estado,
                jugadorNombre = state.jugador?.nombre ?: "",
                numeroCamiseta = state.jugador?.numeroCamiseta ?: 0
            )

            pagoRepository.registrarPago(nuevoPago)

            state.jugador?.let { j ->
                val jActualizado = j.copy(
                    totalGenerado = j.totalGenerado + montoTotal,
                    totalPagado = j.totalPagado + montoAbonado,
                    deudaActual = j.deudaActual + deuda,
                    // Actualizamos también los datos de suscripción al registrar el pago
                    tipoInscripcion = state.tipoInscripcion,
                    fechaInicio = state.fechaInicio,
                    fechaVencimiento = state.fechaVencimiento,
                    cuota = montoTotal
                )
                jugadorRepository.guardarJugador(jActualizado)
            }

            _uiState.update { it.copy(conceptoInput = "", montoTotalInput = "", montoAbonadoInput = "") }
        }
    }

    private fun registrarAbono() {
        viewModelScope.launch {
            val state = _uiState.value
            val pago = state.selectedPagoParaAbono ?: return@launch
            val montoAbono = state.montoNuevoAbonoInput.toDoubleOrNull() ?: 0.0

            if (montoAbono <= 0 || montoAbono > pago.deuda) return@launch

            val nuevoMontoPagado = pago.montoPagado + montoAbono
            val nuevaDeuda = (pago.montoTotal - nuevoMontoPagado).coerceAtLeast(0.0)
            
            val nuevoEstado = if (nuevaDeuda <= 0.0) "PAGADO" else "ABONADO"

            val pagoActualizado = pago.copy(
                montoPagado = nuevoMontoPagado,
                deuda = nuevaDeuda,
                estado = nuevoEstado
            )

            _uiState.update { it.copy(isLoading = true, showAbonoDialog = false) }
            
            pagoRepository.registrarPago(pagoActualizado)

            state.jugador?.let { j ->
                val jActualizado = j.copy(
                    totalPagado = j.totalPagado + montoAbono,
                    deudaActual = (j.deudaActual - montoAbono).coerceAtLeast(0.0)
                )
                jugadorRepository.guardarJugador(jActualizado)
            }
        }
    }

    private fun marcarComoPagado(pago: Pago) {
        viewModelScope.launch {
            if (pago.deuda <= 0) return@launch

            val abonoRestante = pago.deuda

            val pagoActualizado = pago.copy(
                montoPagado = pago.montoTotal,
                deuda = 0.0,
                estado = "PAGADO"
            )
            pagoRepository.registrarPago(pagoActualizado)

            _uiState.value.jugador?.let { j ->
                val nuevaDeuda = (j.deudaActual - abonoRestante).coerceAtLeast(0.0)
                val jActualizado = j.copy(
                    totalPagado = j.totalPagado + abonoRestante,
                    deudaActual = nuevaDeuda
                )
                jugadorRepository.guardarJugador(jActualizado)
            }
        }
    }
}