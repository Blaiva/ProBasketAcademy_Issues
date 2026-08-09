package com.probasketacademy.presentacion.categorias.list

sealed interface CategoriasListEvent {
    data class OnNombreCategoriaChanged(val nombre: String) : CategoriasListEvent
    data class OnShowDialogChanged(val show: Boolean) : CategoriasListEvent
    object OnGuardarCategoria : CategoriasListEvent
}