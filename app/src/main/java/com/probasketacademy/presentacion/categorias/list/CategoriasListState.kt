package com.probasketacademy.presentacion.categorias.list

import com.probasketacademy.domain.model.Categoria

data class CategoriasListState(
    val isLoading: Boolean = false,
    val categorias: List<Categoria> = emptyList(),
    val nombreCategoria: String = "",
    val nombreError: String? = null,
    val errorMessage: String? = null,
    val isSaving: Boolean = false,
    val showDialog: Boolean = false,
)