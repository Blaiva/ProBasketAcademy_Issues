package com.probasketacademy.presentacion.asistencias

import com.probasketacademy.domain.model.Jugador

data class AsistenciasState(
    val isLoading: Boolean = false,
    val categoriaNombre: String = "U-16 Competitivo",
    val jugadores: List<Jugador> = emptyList(),
    val asistencias: Map<Long, Boolean> = emptyMap(),
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)