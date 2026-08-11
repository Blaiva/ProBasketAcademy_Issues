package com.probasketacademy.presentacion.eventos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.probasketacademy.domain.model.Evento
import com.probasketacademy.domain.repository.EventoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class EventosViewModel @Inject constructor(
    private val eventoRepository: EventoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EventosState())
    val uiState: StateFlow<EventosState> = _uiState.asStateFlow()

    init {
        cargarEventosDelDia(_uiState.value.selectedDate)
    }

    fun onEvent(event: EventosEvent) {
        when (event) {
            is EventosEvent.OnDateSelected -> {
                _uiState.update { it.copy(selectedDate = event.date) }
                cargarEventosDelDia(event.date)
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
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // Calculamos el inicio y fin del día en milisegundos
            val startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
            val endOfDay = date.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

            eventoRepository.obtenerEventosPorDia(startOfDay, endOfDay)
                .catch { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
                .collect { eventos ->
                    _uiState.update { it.copy(isLoading = false, eventosDelDia = eventos) }
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
            cargarEventosDelDia(selectedDate) // Recargamos los eventos del día
        }
    }
}