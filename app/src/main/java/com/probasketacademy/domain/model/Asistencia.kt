package com.probasketacademy.domain.model

data class Asistencia(
    val id: Long = 0,
    val jugadorId: Long = 0,
    val categoriaId: Long? = null,
    val fechaEpocaMs: Long = 0,
    val asistio: Boolean = false,
    val nombreJugador: String = "",
    val fotoUri: String? = null
)