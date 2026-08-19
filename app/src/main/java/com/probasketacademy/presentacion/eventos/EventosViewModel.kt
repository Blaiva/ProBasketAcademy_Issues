package com.probasketacademy.presentacion.eventos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.probasketacademy.domain.model.Evento
import com.probasketacademy.domain.usecase.evento.EliminarEventoUseCase
import com.probasketacademy.domain.usecase.evento.GuardarEventoUseCase
import com.probasketacademy.domain.usecase.evento.ObtenerEventosPorDiaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class EventosViewModel @Inject constructor(
    private val obtenerEventosPorDiaUseCase: ObtenerEventosPorDiaUseCase,
    private val guardarEventoUseCase: GuardarEventoUseCase,
    private val eliminarEventoUseCase: EliminarEventoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventosState())
    val uiState: StateFlow<EventosState> = _uiState.asStateFlow()

    private var dayJob: Job? = null
    private var monthJob: Job? = null

    init {
        cargarEventosDelDia(_uiState.value.selectedDate)
        cargarEventosDelMes(_uiState.value.currentYearMonth)
    }

    fun onEvent(event: EventosEvent) {
        when (event) {
            is EventosEvent.OnDateSelected -> {
                _uiState.update { it.copy(selectedDate = event.date) }
                cargarEventosDelDia(event.date)
            }
            is EventosEvent.OnVisibleMonthChanged -> {
                _uiState.update { it.copy(currentYearMonth = event.yearMonth) }
                cargarEventosDelMes(event.yearMonth)
            }
            is EventosEvent.OnToggleAddDialog -> {
                _uiState.update { it.copy(showAddDialog = !it.showAddDialog) }
            }
            is EventosEvent.OnGuardarEvento -> guardarEvento(event)
            is EventosEvent.OnEventoClicked -> {
                _uiState.update { it.copy(eventoSeleccionado = event.evento, showEditDialog = true) }
            }
            is EventosEvent.OnToggleEditDialog -> {
                _uiState.update {
                    it.copy(
                        showEditDialog = !it.showEditDialog,
                        eventoSeleccionado = if (it.showEditDialog) null else it.eventoSeleccionado
                    )
                }
            }
            is EventosEvent.OnActualizarEvento -> actualizarEvento(event)
            is EventosEvent.OnEliminarEvento -> eliminarEvento()
        }
    }

    private fun cargarEventosDelDia(date: LocalDate) {
        dayJob?.cancel()
        dayJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val endOfDay = date.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            obtenerEventosPorDiaUseCase(startOfDay, endOfDay)
                .catch { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
                .collect { eventos -> _uiState.update { it.copy(isLoading = false, eventosDelDia = eventos) } }
        }
    }

    private fun cargarEventosDelMes(yearMonth: YearMonth) {
        monthJob?.cancel()
        monthJob = viewModelScope.launch {
            val startOfMonth = yearMonth.minusMonths(1).atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val endOfMonth = yearMonth.plusMonths(1).atEndOfMonth().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            obtenerEventosPorDiaUseCase(startOfMonth, endOfMonth).collect { eventos ->
                val dias = eventos.map {
                    java.time.Instant.ofEpochMilli(it.fechaHoraEpocaMs).atZone(ZoneId.systemDefault()).toLocalDate()
                }.toSet()
                _uiState.update { it.copy(diasConEventos = dias) }
            }
        }
    }

    private fun guardarEvento(event: EventosEvent.OnGuardarEvento) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val selectedDate = _uiState.value.selectedDate
            val dateTime = LocalDateTime.of(selectedDate, event.time)
            val epochMs = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            val nuevoEvento = Evento(
                titulo = event.titulo,
                tipo = event.tipo,
                fechaHoraEpocaMs = epochMs,
                duracionHoras = event.duracion,
                lugar = event.lugar
            )

            guardarEventoUseCase(nuevoEvento)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, showAddDialog = false, errorMessage = null) }
                    cargarEventosDelDia(selectedDate)
                    cargarEventosDelMes(_uiState.value.currentYearMonth)
                }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
        }
    }

    private fun actualizarEvento(event: EventosEvent.OnActualizarEvento) {
        val eventoActual = _uiState.value.eventoSeleccionado ?: return
        viewModelScope.launch {
            val dateTime = LocalDateTime.of(_uiState.value.selectedDate, event.time)
            val epochMs = dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            val eventoActualizado = eventoActual.copy(
                titulo = event.titulo,
                tipo = event.tipo,
                fechaHoraEpocaMs = epochMs,
                duracionHoras = event.duracion,
                lugar = event.lugar
            )

            guardarEventoUseCase(eventoActualizado)
                .onSuccess {
                    _uiState.update { it.copy(showEditDialog = false, eventoSeleccionado = null) }
                    cargarEventosDelDia(_uiState.value.selectedDate)
                    cargarEventosDelMes(_uiState.value.currentYearMonth)
                }
                .onFailure { e -> _uiState.update { it.copy(errorMessage = e.message) } }
        }
    }

    private fun eliminarEvento() {
        val evento = _uiState.value.eventoSeleccionado ?: return
        viewModelScope.launch {
            eliminarEventoUseCase(evento.id)
            _uiState.update { it.copy(showEditDialog = false, eventoSeleccionado = null) }
            cargarEventosDelDia(_uiState.value.selectedDate)
            cargarEventosDelMes(_uiState.value.currentYearMonth)
        }
    }
}