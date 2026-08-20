package com.probasketacademy.presentacion.pagos.detalle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.probasketacademy.domain.model.Pago
import com.probasketacademy.domain.repository.JugadorRepository
import com.probasketacademy.domain.usecase.pago.DatosPagoJugador
import com.probasketacademy.domain.usecase.pago.MarcarPagoComoPagadoUseCase
import com.probasketacademy.domain.usecase.pago.ObtenerPagosPorJugadorUseCase
import com.probasketacademy.domain.usecase.pago.RegistrarAbonoUseCase
import com.probasketacademy.domain.usecase.pago.RegistrarPagoJugadorUseCase
import com.probasketacademy.presentacion.pagos.detalle.PagosDetalleEvent
import com.probasketacademy.presentacion.pagos.detalle.PagosDetalleState
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
    private val obtenerPagosPorJugadorUseCase: ObtenerPagosPorJugadorUseCase,
    private val registrarPagoJugadorUseCase: RegistrarPagoJugadorUseCase,
    private val registrarAbonoUseCase: RegistrarAbonoUseCase,
    private val marcarPagoComoPagadoUseCase: MarcarPagoComoPagadoUseCase
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
                _uiState.update {
                    it.copy(
                        showAbonoDialog = !it.showAbonoDialog,
                        selectedPagoParaAbono = event.pago,
                        montoNuevoAbonoInput = "",
                        montoNuevoAbonoError = null
                    )
                }
            }
            is PagosDetalleEvent.OnConceptoChanged -> _uiState.update { it.copy(conceptoInput = event.concepto) }
            is PagosDetalleEvent.OnMontoTotalChanged -> _uiState.update { it.copy(montoTotalInput = event.monto) }
            is PagosDetalleEvent.OnMontoAbonadoChanged -> _uiState.update { it.copy(montoAbonadoInput = event.monto) }
            is PagosDetalleEvent.OnMontoNuevoAbonoChanged -> {
                _uiState.update {
                    it.copy(
                        montoNuevoAbonoInput = event.monto.filter { c -> c.isDigit() },
                        montoNuevoAbonoError = null
                    )
                }
            }
            is PagosDetalleEvent.OnTipoInscripcionChanged -> {
                _uiState.update { it.copy(tipoInscripcion = event.value) }
                calcularVencimiento()
            }
            is PagosDetalleEvent.OnFechaInicioChanged -> {
                _uiState.update { it.copy(fechaInicio = event.value) }
                calcularVencimiento()
            }
            is PagosDetalleEvent.OnRegistrarPago -> registrarNuevoPago()
            is PagosDetalleEvent.OnRegistrarAbono -> registrarAbono()
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
            e.printStackTrace()
        }
    }

    fun cargarDatos(id: Long) {
        this.jugadorId = id
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            jugadorRepository.obtenerJugadorPorId(jugadorId).collectLatest { jugador ->
                _uiState.update { it.copy(jugador = jugador) }
                if (jugador != null) cargarPagos()
            }
        }
    }

    private fun cargarPagos() {
        viewModelScope.launch {
            obtenerPagosPorJugadorUseCase(jugadorId)
                .catch { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
                .collect { listaPagos ->
                    val saldo = listaPagos.sumOf { it.deuda }
                    val pagosOrdenados = listaPagos.sortedByDescending { it.estado != "PAGADO" }
                    _uiState.update { it.copy(isLoading = false, pagos = pagosOrdenados, saldoPendiente = saldo) }
                }
        }
    }

    private fun registrarNuevoPago() {
        val state = _uiState.value
        val jugador = state.jugador ?: return
        val montoTotal = state.montoTotalInput.toDoubleOrNull() ?: 0.0
        val montoAbonado = state.montoAbonadoInput.toDoubleOrNull() ?: 0.0
        val concepto = state.conceptoInput

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showPagoDialog = false) }
            val sdfPago = SimpleDateFormat("dd MMM yyyy", Locale.Builder().setLanguage("es").setRegion("ES").build())
            val fechaActual = sdfPago.format(Date())

            registrarPagoJugadorUseCase(
                DatosPagoJugador(
                    jugador = jugador,
                    concepto = concepto,
                    montoTotal = montoTotal,
                    montoAbonado = montoAbonado,
                    fecha = fechaActual,
                    tipoInscripcion = state.tipoInscripcion,
                    fechaInicio = state.fechaInicio,
                    fechaVencimiento = state.fechaVencimiento
                )
            ).onSuccess {
                _uiState.update { it.copy(isLoading = false, conceptoInput = "", montoTotalInput = "", montoAbonadoInput = "") }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    private fun registrarAbono() {
        val state = _uiState.value
        val pago = state.selectedPagoParaAbono ?: return
        val jugador = state.jugador ?: return
        val montoAbono = state.montoNuevoAbonoInput.toDoubleOrNull()

        if (montoAbono == null || montoAbono <= 0.0) {
            _uiState.update { it.copy(montoNuevoAbonoError = "Ingresa un monto válido") }
            return
        }

        viewModelScope.launch {
            registrarAbonoUseCase(pago, jugador, montoAbono)
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            showAbonoDialog = false,
                            selectedPagoParaAbono = null,
                            montoNuevoAbonoInput = "",
                            montoNuevoAbonoError = null
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(montoNuevoAbonoError = e.message) }
                }
        }
    }

    private fun marcarComoPagado(pago: Pago) {
        val jugador = _uiState.value.jugador ?: return
        viewModelScope.launch {
            marcarPagoComoPagadoUseCase(pago, jugador)
                .onFailure { e -> _uiState.update { it.copy(errorMessage = e.message) } }
        }
    }
}