package com.probasketacademy.presentacion.asistencias

import java.time.LocalDate
import java.time.YearMonth

sealed interface AsistenciasEvent {
    data class OnJugadorToggled(val jugadorId: Long, val asistio: Boolean) : AsistenciasEvent
    data object OnConfirmarAsistencia : AsistenciasEvent
    data class OnDateSelected(val date: LocalDate) : AsistenciasEvent
    data class OnVisibleMonthChanged(val yearMonth: YearMonth) : AsistenciasEvent
    data class OnCategoriaSelected(val id: Long, val nombre: String) : AsistenciasEvent
    data object OnResetGuardado : AsistenciasEvent // <-- NUEVO EVENTO PARA EVITAR EL BUG DE NAVEGACIÓN
}