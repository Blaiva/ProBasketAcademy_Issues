package com.probasketacademy.presentacion.asistencias

import com.probasketacademy.domain.model.Categoria
import com.probasketacademy.domain.model.Jugador
import java.time.LocalDate
import java.time.YearMonth

data class AsistenciasState(
    val isLoading: Boolean = false,
    val categorias: List<Categoria> = emptyList(),
    val categoriaSeleccionadaId: Long? = null,
    val categoriaSeleccionadaNombre: String = "Selecciona una categoría",
    val jugadores: List<Jugador> = emptyList(),
    val asistencias: Map<Long, Boolean> = emptyMap(),
    val selectedDate: LocalDate = LocalDate.now(),
    val currentYearMonth: YearMonth = YearMonth.now(),
    val isEditable: Boolean = true,
    val showQuitarConfirmDialog: Boolean = false,
    val jugadorParaQuitar: Jugador? = null,
    val errorMessage: String? = null
)