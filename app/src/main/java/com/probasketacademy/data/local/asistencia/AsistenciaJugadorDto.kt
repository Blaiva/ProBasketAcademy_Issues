package com.probasketacademy.data.local.asistencia

data class AsistenciaJugadorDto(
    val jugadorId: Long,
    val nombreJugador: String,
    val fotoUri: String?,
    val asistio: Boolean?
)