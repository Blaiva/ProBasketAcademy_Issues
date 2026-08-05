package com.probasketacademy.presentacion.asistencias

sealed interface AsistenciasEvent {
    data class OnJugadorToggled(val jugadorId: Long, val asistio: Boolean) : AsistenciasEvent
    data object OnConfirmarAsistencia : AsistenciasEvent
}