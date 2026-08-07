package com.probasketacademy.presentacion.eventos

import com.probasketacademy.domain.model.Evento
import java.time.LocalDate
import java.time.YearMonth

data class EventosState(
    val isLoading: Boolean = false,
    val selectedDate: LocalDate = LocalDate.now(),
    val currentYearMonth: YearMonth = YearMonth.now(),
    val eventosDelDia: List<Evento> = emptyList(),
    val showAddDialog: Boolean = false,
    val errorMessage: String? = null
)