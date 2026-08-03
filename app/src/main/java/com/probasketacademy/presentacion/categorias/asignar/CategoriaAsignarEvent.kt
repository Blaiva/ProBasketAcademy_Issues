package com.probasketacademy.presentacion.categorias.asignar

sealed interface CategoriaAsignarEvent {
    data class OnSearchQueryChanged(val query: String) : CategoriaAsignarEvent
    data class OnJugadorToggled(val jugadorId: Long, val isSelected: Boolean) : CategoriaAsignarEvent
    data object OnGuardarAsignacion : CategoriaAsignarEvent
}