package com.probasketacademy.presentacion.eventos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.probasketacademy.domain.model.Evento
import com.probasketacademy.domain.repository.EventoRepository
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
    private val eventoRepository: EventoRepository
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
            is EventosEvent.OnGuardarEvento -> {
                guardarEvento(event)
            }
        }
    }

    private fun cargarEventosDelDia(date: LocalDate) {
        dayJob?.cancel()
        dayJob = viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val endOfDay = date.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            eventoRepository.obtenerEventosPorDia(startOfDay, endOfDay)
                .catch { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
                .collect { eventos ->
                    _uiState.update { it.copy(isLoading = false, eventosDelDia = eventos) }
                }
        }
    }

    private fun cargarEventosDelMes(yearMonth: YearMonth) {
        monthJob?.cancel()
        monthJob = viewModelScope.launch {
            // Buscamos desde el mes anterior hasta el siguiente para cubrir toda la vista de la cuadrícula
            val startOfMonth = yearMonth.minusMonths(1).atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val endOfMonth = yearMonth.plusMonths(1).atEndOfMonth().atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            eventoRepository.obtenerEventosPorDia(startOfMonth, endOfMonth)
                .collect { eventos ->
                    // Extraemos solo las fechas únicas que tienen al menos un evento
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

            eventoRepository.guardarEvento(nuevoEvento)
            _uiState.update { it.copy(showAddDialog = false) }
            // Al guardar, refrescamos tanto el día como los puntitos del mes
            cargarEventosDelDia(selectedDate)
            cargarEventosDelMes(_uiState.value.currentYearMonth)
        }
    }
}