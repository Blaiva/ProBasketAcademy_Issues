package com.probasketacademy.presentacion.navegacion

import kotlinx.serialization.Serializable

sealed interface Screen {
    @Serializable
    data object Auth : Screen

    @Serializable
    data object Home : Screen

    @Serializable
    data object Categorias : Screen

    @Serializable
    data object Jugadores : Screen

    @Serializable
    data object Asistencias : Screen

    @Serializable
    data object Pagos : Screen

    @Serializable
    data object Eventos : Screen

    // Agregamos la ruta para editar/ver detalle del jugador, que recibe un ID
    @Serializable
    data class JugadorEdit(val jugadorId: Long) : Screen
}