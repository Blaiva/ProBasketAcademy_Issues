package com.probasketacademy.data.repository

import com.probasketacademy.data.local.categoria.CategoriaDao
import com.probasketacademy.data.mapper.toDomain
import com.probasketacademy.data.mapper.toEntity
import com.probasketacademy.domain.model.Categoria
import com.probasketacademy.domain.repository.CategoriaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoriaRepositoryImpl @Inject constructor(private val categoriaDao: CategoriaDao): CategoriaRepository {
    override fun obtenerCategoriasConConteo(): Flow<List<Categoria>> {
        return categoriaDao.obtenerCategoriasConConteo().map { lista -> lista.map { it.toDomain() } }
    }

    override suspend fun guardarCategoria(categoria: Categoria): Long {
        categoriaDao.guardarCategoria(categoria.toEntity())
        return categoria.id ?: 0
    }

    override fun obtenerCategoriaPorId(id: Long): Flow<Categoria?> {
        return categoriaDao.obtenerCategoriaConConteoPorId(id).map { dto ->
            dto?.toDomain()
        }
    }

    override suspend fun eliminarCategoria(id: Long) {
        categoriaDao.eliminarCategoria(id)
    }
}