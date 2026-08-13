package com.probasketacademy.presentacion.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.probasketacademy.domain.repository.AsistenciaRepository
import com.probasketacademy.domain.repository.JugadorRepository
import com.probasketacademy.domain.repository.PagoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val pagoRepository: PagoRepository,
    private val asistenciaRepository: AsistenciaRepository,
    private val jugadorRepository: JugadorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    init {
        cargarDatosDelDashboard()
    }

    private fun cargarDatosDelDashboard() {
        _uiState.update { it.copy(isLoading = true) }

        val yearMonth = YearMonth.now()
        val inicioMes =
            yearMonth.atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
        val finMes = yearMonth.atEndOfMonth().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault())
            .toInstant().toEpochMilli()

        viewModelScope.launch {
            // 1. Cargar Total de Jugadores Activos
            jugadorRepository.obtenerJugadores().collectLatest { jugadores ->
                val activos = jugadores.count { it.estado.equals("Activo", ignoreCase = true) }
                _uiState.update { it.copy(jugadoresActivos = activos) }
            }
        }

        viewModelScope.launch {
            // 2. Cargar Asistencia Promedio del Mes
            asistenciaRepository.obtenerAsistenciaPromedioPorMes(inicioMes, finMes)
                .collectLatest { promedio ->
                    val promStr = if (promedio != null) "${promedio.toInt()}%" else "0%"
                    _uiState.update { it.copy(asistenciaPromedio = promStr) }
                }
        }

        viewModelScope.launch {
            // 3. Cargar Cobros Pendientes
            pagoRepository.obtenerCobrosPendientes().collectLatest { pagosPendientes ->
                _uiState.update { it.copy(cobrosPendientes = pagosPendientes) }
            }
        }

        viewModelScope.launch {
            // 4. Calcular Ingresos Reales Totales
            pagoRepository.obtenerIngresosTotales().collectLatest { total ->
                val ingresos = total ?: 0.0
                val formatoMoneda = NumberFormat.getNumberInstance(Locale("es", "DO"))
                val ingresoStr = "$${formatoMoneda.format(ingresos)}"

                _uiState.update {
                    it.copy(isLoading = false, ingresosMes = ingresoStr)
                }
            }
        }
    }
}