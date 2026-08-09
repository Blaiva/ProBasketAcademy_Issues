package com.probasketacademy.domain.usecase.categoria

import com.probasketacademy.domain.model.Categoria
import com.probasketacademy.domain.repository.CategoriaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObtenerCategoriaPorIdUseCase @Inject constructor(
    private val categoriaRepository: CategoriaRepository
) {
    operator fun invoke(id: Long): Flow<Categoria?> {
        return categoriaRepository.obtenerCategoriaPorId(id)
    }
}