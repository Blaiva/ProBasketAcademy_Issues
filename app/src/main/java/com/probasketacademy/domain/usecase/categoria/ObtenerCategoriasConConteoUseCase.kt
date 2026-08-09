package com.probasketacademy.domain.usecase.categoria

import com.probasketacademy.domain.model.Categoria
import com.probasketacademy.domain.repository.CategoriaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObtenerCategoriasConConteoUseCase @Inject constructor(
    private val repository: CategoriaRepository
) {
    operator fun invoke(): Flow<List<Categoria>> = repository.obtenerCategoriasConConteo()
}