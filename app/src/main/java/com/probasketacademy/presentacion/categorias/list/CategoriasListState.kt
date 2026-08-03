package com.probasketacademy.presentacion.categorias.list

import com.probasketacademy.domain.model.Categoria

data class CategoriasListState(
    val isLoading: Boolean = false,
    val categorias: List<Categoria> = emptyList(),
    val errorMessage: String? = null
)