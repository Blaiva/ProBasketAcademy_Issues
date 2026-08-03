package com.probasketacademy.presentacion.categorias.list

sealed interface CategoriasListEvent {
    data class OnAsignarJugadorClicked(val categoriaId: Long) : CategoriasListEvent
    data class OnVerEditarClicked(val categoriaId: Long) : CategoriasListEvent
    data class OnGuardarCategoria(val nombre: String) : CategoriasListEvent // Nuevo evento
}