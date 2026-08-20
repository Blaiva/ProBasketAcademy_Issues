package com.probasketacademy.presentacion.asistencias

import com.probasketacademy.domain.model.Jugador
import java.time.LocalDate
import java.time.YearMonth

sealed interface AsistenciasEvent {
    data class OnJugadorToggled(val jugadorId: Long) : AsistenciasEvent
    data object OnConfirmarAsistencia : AsistenciasEvent
    data class OnDateSelected(val date: LocalDate) : AsistenciasEvent
    data class OnVisibleMonthChanged(val yearMonth: YearMonth) : AsistenciasEvent
    data class OnCategoriaSelected(val id: Long, val nombre: String) : AsistenciasEvent
    data object OnResetGuardado : AsistenciasEvent
    data class OnSolicitarQuitarAsistencia(val jugador: Jugador) : AsistenciasEvent
    data object OnConfirmarQuitarAsistencia : AsistenciasEvent
    data object OnCancelarQuitarAsistencia : AsistenciasEvent
}