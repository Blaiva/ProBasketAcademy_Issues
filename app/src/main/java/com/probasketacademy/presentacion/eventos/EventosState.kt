package com.probasketacademy.presentacion.eventos

import com.probasketacademy.domain.model.Evento
import java.time.LocalDate
import java.time.YearMonth

data class EventosState(
    val eventosDelDia: List<Evento> = emptyList(),
    val selectedDate: LocalDate = LocalDate.now(),
    val currentYearMonth: YearMonth = YearMonth.now(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showAddDialog: Boolean = false
)