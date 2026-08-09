package com.probasketacademy.presentacion.categorias.detalle

import com.probasketacademy.domain.model.Jugador

sealed interface CategoriaDetalleEvent {
    data class OnCargarDetalle(val categoriaId: Long) : CategoriaDetalleEvent
    data class OnNombreCategoriaChanged(val nombre: String) : CategoriaDetalleEvent
    object OnGuardarNombreCategoria : CategoriaDetalleEvent
    data class OnShowAddJugadoresDialogChanged(val show: Boolean) : CategoriaDetalleEvent
    data class OnJugadorSelectionToggled(val jugadorId: Long) : CategoriaDetalleEvent
    object OnAsignarJugadoresSeleccionados : CategoriaDetalleEvent
    data class OnRemoverJugador(val jugador: Jugador) : CategoriaDetalleEvent
}