package com.probasketacademy.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.probasketacademy.data.local.categoria.CategoriaDao
import com.probasketacademy.data.mapper.toDomain
import com.probasketacademy.data.mapper.toEntity
import com.probasketacademy.domain.model.Categoria
import com.probasketacademy.domain.repository.CategoriaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoriaRepositoryImpl @Inject constructor(
    private val categoriaDao: CategoriaDao,
    private val auth: FirebaseAuth
): CategoriaRepository {
    private val userId: String get() = auth.currentUser?.uid ?: ""

    override fun obtenerCategoriasConConteo(): Flow<List<Categoria>> {
        return categoriaDao.obtenerCategoriasConConteo(userId).map { lista -> lista.map { it.toDomain() } }
    }

    override suspend fun guardarCategoria(categoria: Categoria): Long {
        categoriaDao.guardarCategoria(categoria.toEntity(userId))
        return categoria.id
    }

    override fun obtenerCategoriaPorId(id: Long): Flow<Categoria?> {
        return categoriaDao.obtenerCategoriaConConteoPorId(id, userId).map { dto -> dto?.toDomain() }
    }

    override suspend fun eliminarCategoria(id: Long) {
        categoriaDao.eliminarCategoria(id, userId)
    }
}