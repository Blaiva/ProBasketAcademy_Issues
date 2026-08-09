package com.probasketacademy.presentacion.categorias.detalle

import com.probasketacademy.domain.model.Jugador

data class CategoriaDetalleState(
    val isLoading: Boolean = false,
    val categoriaId: Long = 0L,
    val nombreCategoria: String = "",
    val nombreError: String? = null,
    val isSavingNombre: Boolean = false,
    val jugadoresAsignados: List<Jugador> = emptyList(),
    val jugadoresSinCategoria: List<Jugador> = emptyList(),
    val selectedJugadoresIds: Set<Long> = emptySet(),
    val showAddJugadoresDialog: Boolean = false,
    val isAssigning: Boolean = false,
    val errorMessage: String? = null
)