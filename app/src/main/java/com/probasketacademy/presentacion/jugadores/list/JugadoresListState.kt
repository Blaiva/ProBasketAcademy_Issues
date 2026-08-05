package com.probasketacademy.presentacion.jugadores.list

import com.probasketacademy.domain.model.Jugador

data class JugadoresListState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val jugadores: List<Jugador> = emptyList(),
    val errorMessage: String? = null
)