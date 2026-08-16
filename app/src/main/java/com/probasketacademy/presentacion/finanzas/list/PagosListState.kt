package com.probasketacademy.presentacion.finanzas.list

import com.probasketacademy.domain.model.Jugador

data class PagosListState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val jugadores: List<Jugador> = emptyList(),
    val totalGeneradoGlobal: Double = 0.0,
    val totalPagadoGlobal: Double = 0.0,
    val deudaGlobal: Double = 0.0,
    val errorMessage: String? = null
)