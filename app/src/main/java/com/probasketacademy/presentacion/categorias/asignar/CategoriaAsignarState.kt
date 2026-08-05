package com.probasketacademy.presentacion.categorias.asignar

import com.probasketacademy.domain.model.Jugador

data class CategoriaAsignarState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val jugadores: List<Jugador> = emptyList(),
    val seleccionados: Set<Long> = emptySet(), // IDs de los jugadores que vamos a asignar
    val isSaved: Boolean = false,
    val errorMessage: String? = null
)