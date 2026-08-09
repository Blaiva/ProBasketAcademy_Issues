package com.probasketacademy.presentacion.navegacion

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed class Screen: NavKey {
    @Serializable
    data object Auth : Screen()

    @Serializable
    data object Home : Screen()

    @Serializable
    data object Categorias : Screen()

    @Serializable
    data object Jugadores : Screen()

    @Serializable
    data object Asistencias : Screen()

    @Serializable
    data object Pagos : Screen()

    @Serializable
    data object Eventos : Screen()

    // Agregamos la ruta para editar/ver detalle del jugador, que recibe un ID
    @Serializable
    data class JugadorEdit(val jugadorId: Long) : Screen()

    // --- NUEVAS RUTAS PARA CATEGORÍAS ---
    @Serializable
    data class CategoriaAsignar(val categoriaId: Long) : Screen()

    @Serializable
    data class CategoriaDetalle(val categoriaId: Long) : Screen()
}