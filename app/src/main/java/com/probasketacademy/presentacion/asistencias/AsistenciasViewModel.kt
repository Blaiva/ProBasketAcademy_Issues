package com.probasketacademy.presentacion.asistencias

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.probasketacademy.domain.model.Asistencia
import com.probasketacademy.domain.model.Jugador
import com.probasketacademy.domain.usecase.asistencia.ActualizarAsistenciaJugadorUseCase
import com.probasketacademy.domain.usecase.asistencia.ObtenerListaAsistenciaPorCategoriaUseCase
import com.probasketacademy.domain.usecase.asistencia.RegistrarAsistenciasUseCase
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
    private val obtenerListaAsistenciaPorCategoriaUseCase: ObtenerListaAsistenciaPorCategoriaUseCase,
    private val actualizarAsistenciaJugadorUseCase: ActualizarAsistenciaJugadorUseCase,
    private val obtenerCategoriasConConteoUseCase: ObtenerCategoriasConConteoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(AsistenciasState())
    val uiState: StateFlow<AsistenciasState> = _uiState.asStateFlow()

    private var job: Job? = null

    init {
        cargarCategorias()
    }

    fun onEvent(event: AsistenciasEvent) {
        when (event) {
            is AsistenciasEvent.OnJugadorToggled -> marcarPresente(event.jugadorId)
            is AsistenciasEvent.OnSolicitarQuitarAsistencia -> {
                _uiState.update { it.copy(showQuitarConfirmDialog = true, jugadorParaQuitar = event.jugador) }
            }
            is AsistenciasEvent.OnCancelarQuitarAsistencia -> {
                _uiState.update { it.copy(showQuitarConfirmDialog = false, jugadorParaQuitar = null) }
            }
            is AsistenciasEvent.OnConfirmarQuitarAsistencia -> quitarAsistencia()
            is AsistenciasEvent.OnDateSelected -> {
                _uiState.update { it.copy(selectedDate = event.date) }
                cargarDatosPorFecha(event.date)
            }
            is AsistenciasEvent.OnVisibleMonthChanged -> {
                _uiState.update { it.copy(currentYearMonth = event.yearMonth) }
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
            is AsistenciasEvent.OnConfirmarAsistencia -> {
                _uiState.update { it.copy(isSaved = true) }
            }
            is AsistenciasEvent.OnResetGuardado -> {
                _uiState.update { it.copy(isSaved = false) }
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

            obtenerListaAsistenciaPorCategoriaUseCase(categoriaId, fechaTimestamp)
                .catch { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
                .collect { lista ->
                    val jugadoresDeLista = lista.map {
                        Jugador(
                            jugadorId = it.jugadorId,
                            nombre = it.nombreJugador,
                            fotoUri = it.fotoUri,
                            categoriaId = categoriaId
                        )
                    }
                    val asistenciasMap = lista.associate { it.jugadorId to it.asistio }
                    _uiState.update {
                        it.copy(isLoading = false, jugadores = jugadoresDeLista, asistencias = asistenciasMap)
                    }
                }
        }
    }

    private fun marcarPresente(jugadorId: Long) {
        if (!_uiState.value.isEditable) return
        val categoriaId = _uiState.value.categoriaSeleccionadaId ?: return
        val jugador = _uiState.value.jugadores.find { it.jugadorId == jugadorId } ?: return

        viewModelScope.launch {
            val fechaTimestamp = _uiState.value.selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            actualizarAsistenciaJugadorUseCase(
                jugadorId = jugadorId,
                categoriaId = categoriaId,
                fechaTimestamp = fechaTimestamp,
                asistio = true,
                nombreJugador = jugador.nombre
            )
        }
    }

    private fun quitarAsistencia() {
        if (!_uiState.value.isEditable) return
        val categoriaId = _uiState.value.categoriaSeleccionadaId ?: return
        val jugador = _uiState.value.jugadorParaQuitar ?: return

        viewModelScope.launch {
            val fechaTimestamp = _uiState.value.selectedDate.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            actualizarAsistenciaJugadorUseCase(
                jugadorId = jugador.jugadorId,
                categoriaId = categoriaId,
                fechaTimestamp = fechaTimestamp,
                asistio = false,
                nombreJugador = jugador.nombre
            )
            _uiState.update { it.copy(showQuitarConfirmDialog = false, jugadorParaQuitar = null) }
        }
    }
}