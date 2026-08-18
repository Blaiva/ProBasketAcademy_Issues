package com.probasketacademy.presentacion.asistencias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.probasketacademy.domain.model.Asistencia
import com.probasketacademy.domain.repository.AsistenciaRepository
import com.probasketacademy.domain.repository.JugadorRepository
import com.probasketacademy.domain.usecase.categoria.ObtenerCategoriasConConteoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class AsistenciasViewModel @Inject constructor(
    private val jugadorRepository: JugadorRepository,
    private val asistenciaRepository: AsistenciaRepository,
    private val obtenerCategoriasConConteoUseCase: ObtenerCategoriasConConteoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AsistenciasState())
    val uiState: StateFlow<AsistenciasState> = _uiState.asStateFlow()

    private var job: Job? = null
    private var asistenciasActuales: List<Asistencia> = emptyList()

    init {
        cargarCategorias()
    }

    fun onEvent(event: AsistenciasEvent) {
        when (event) {
            is AsistenciasEvent.OnJugadorToggled -> {
                if (!_uiState.value.isEditable) return
                val nuevasAsistencias = _uiState.value.asistencias.toMutableMap()
                nuevasAsistencias[event.jugadorId] = event.asistio
                _uiState.update { it.copy(asistencias = nuevasAsistencias) }
            }
            is AsistenciasEvent.OnConfirmarAsistencia -> guardarAsistencias()
            is AsistenciasEvent.OnDateSelected -> {
                _uiState.update { it.copy(selectedDate = event.date) }
                cargarDatosPorFecha(event.date)
            }
            is AsistenciasEvent.OnVisibleMonthChanged -> {
                _uiState.update { it.copy(currentYearMonth = event.yearMonth) }
            }
            is AsistenciasEvent.OnResetGuardado -> {
                _uiState.update { it.copy(isSaved = false) }
            }
            is AsistenciasEvent.OnCategoriaSelected -> {
                _uiState.update {
                    it.copy(
                        categoriaSeleccionadaId = event.id,
                        categoriaSeleccionadaNombre = event.nombre,
                        jugadores = emptyList(),
                        asistencias = emptyMap()
                    )
                }
                cargarDatosPorFecha(_uiState.value.selectedDate)
            }
        }
    }

    private fun cargarCategorias() {
        viewModelScope.launch {
            obtenerCategoriasConConteoUseCase().collectLatest { lista ->
                _uiState.update { it.copy(categorias = lista) }
            }
        }
    }

    private fun cargarDatosPorFecha(date: LocalDate) {
        val categoriaId = _uiState.value.categoriaSeleccionadaId ?: return
        job?.cancel()
        job = viewModelScope.launch {
            val isToday = date == LocalDate.now()
            _uiState.update { it.copy(isLoading = true, isEditable = isToday) }

            val fechaTimestamp = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

            combine(
                jugadorRepository.obtenerJugadoresPorCategoria(categoriaId),
                asistenciaRepository.obtenerAsistenciasPorDia(fechaTimestamp)
            ) { jugadores, asistencias ->
                asistenciasActuales = asistencias.filter { it.categoriaId == categoriaId }
                val asistenciasMap = jugadores.associate { j ->
                    val asis = asistencias.find { it.jugadorId == j.jugadorId }
                    j.jugadorId to (asis?.asistio ?: false)
                }
                _uiState.update {
                    it.copy(isLoading = false, jugadores = jugadores, asistencias = asistenciasMap)
                }
            }.catch { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }.collect()
        }
    }

    private fun guardarAsistencias() {
        if (!_uiState.value.isEditable) return
        val categoriaId = _uiState.value.categoriaSeleccionadaId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val state = _uiState.value
            val fechaTimestamp = state.selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()

            val registros = state.jugadores.map { jugador ->
                val existente = asistenciasActuales.find { it.jugadorId == jugador.jugadorId }
                Asistencia(
                    id = existente?.id ?: 0L,
                    jugadorId = jugador.jugadorId,
                    categoriaId = categoriaId,
                    fechaEpocaMs = fechaTimestamp,
                    asistio = state.asistencias[jugador.jugadorId] ?: false,
                    nombreJugador = jugador.nombre
                )
            }

            asistenciaRepository.registrarAsistencias(registros)
            _uiState.update { it.copy(isLoading = false, isSaved = true) }
        }
    }
}