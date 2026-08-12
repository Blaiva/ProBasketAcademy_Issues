package com.probasketacademy.presentacion.finanzas.list

import com.probasketacademy.domain.model.Jugador

data class PagosListState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val jugadores: List<Jugador> = emptyList(),
    val errorMessage: String? = null
)