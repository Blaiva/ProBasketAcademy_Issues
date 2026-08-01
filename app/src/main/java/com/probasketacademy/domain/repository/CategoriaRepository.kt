package com.probasketacademy.domain.repository

import com.probasketacademy.domain.model.Categoria
import kotlinx.coroutines.flow.Flow

interface CategoriaRepository {
    fun obtenerCategoriasConConteo(): Flow<List<Categoria>>
    suspend fun guardarCategoria(categoria: Categoria): Long
}