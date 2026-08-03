package com.probasketacademy.presentacion.categorias.detalle

import com.probasketacademy.domain.model.Jugador

data class CategoriaDetalleState(
    val isLoading: Boolean = false,
    val jugadores: List<Jugador> = emptyList(),
    val errorMessage: String? = null
)