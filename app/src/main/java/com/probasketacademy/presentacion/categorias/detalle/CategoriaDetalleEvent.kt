package com.probasketacademy.presentacion.categorias.detalle

sealed interface CategoriaDetalleEvent {
    data class OnRemoverJugador(val jugador: com.probasketacademy.domain.model.Jugador) : CategoriaDetalleEvent
}