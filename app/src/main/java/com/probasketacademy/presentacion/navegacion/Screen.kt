package com.probasketacademy.presentacion.navegacion

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

sealed interface Screen : NavKey {
    @Serializable data object Auth : Screen
    @Serializable data object Home : Screen
    @Serializable data object Categorias : Screen
    @Serializable data object Jugadores : Screen
    @Serializable data object Asistencias : Screen
    @Serializable data object Eventos : Screen

    @Serializable data class JugadorEdit(val jugadorId: Long) : Screen
    @Serializable data class CategoriaAsignar(val categoriaId: Long) : Screen
    @Serializable data class CategoriaDetalle(val categoriaId: Long) : Screen

    @Serializable data object Pagos : Screen
    @Serializable data class PagosDetalle(val jugadorId: Long) : Screen
}