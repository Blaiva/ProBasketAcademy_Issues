package com.probasketacademy.presentacion.asistencias

import com.probasketacademy.domain.model.Jugador
import java.time.LocalDate
import java.time.YearMonth

data class AsistenciasState(
    val isLoading: Boolean = false,
    val categoriaNombre: String = "General",
    val jugadores: List<Jugador> = emptyList(),
    val asistencias: Map<Long, Boolean> = emptyMap(),
    val selectedDate: LocalDate = LocalDate.now(),
    val currentYearMonth: YearMonth = YearMonth.now(),
    val isEditable: Boolean = true, // Controla si es hoy y se puede modificar
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)