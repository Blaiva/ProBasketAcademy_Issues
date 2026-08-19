package com.probasketacademy.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.probasketacademy.data.local.categoria.CategoriaDao
import com.probasketacademy.data.mapper.toDomain
import com.probasketacademy.data.mapper.toEntity
import com.probasketacademy.domain.model.Categoria
import com.probasketacademy.domain.repository.CategoriaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CategoriaRepositoryImpl @Inject constructor(
    private val categoriaDao: CategoriaDao,
    private val userSession: UserSessionProvider
): CategoriaRepository {

    override fun obtenerCategoriasConConteo(): Flow<List<Categoria>> {
        return userSession.observeUserId().flatMapLatest { userId ->
            categoriaDao.obtenerCategoriasConConteo(userId).map { lista -> lista.map { it.toDomain() } }
        }
    }

    override suspend fun guardarCategoria(categoria: Categoria): Long {
        categoriaDao.guardarCategoria(categoria.toEntity(userSession.currentUserId))
        return categoria.id
    }

    override fun obtenerCategoriaPorId(id: Long): Flow<Categoria?> {
        return userSession.observeUserId().flatMapLatest { userId ->
            categoriaDao.obtenerCategoriaConConteoPorId(id, userId).map { it?.toDomain() }
        }
    }

    override suspend fun eliminarCategoria(id: Long) {
        categoriaDao.eliminarCategoria(id, userSession.currentUserId)
    }
}