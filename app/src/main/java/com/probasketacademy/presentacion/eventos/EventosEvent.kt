package com.probasketacademy.presentacion.eventos

import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

sealed interface EventosEvent {
    data class OnDateSelected(val date: LocalDate) : EventosEvent
    data class OnVisibleMonthChanged(val yearMonth: YearMonth) : EventosEvent
    data object OnToggleAddDialog : EventosEvent
    data class OnGuardarEvento(
        val titulo: String,
        val tipo: String,
        val time: LocalTime,
        val duracion: Float,
        val lugar: String
    ) : EventosEvent
}