package com.probasketacademy.domain.usecase.categoria

import com.probasketacademy.domain.model.Categoria
import com.probasketacademy.domain.repository.CategoriaRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GuardarCategoriaUseCase @Inject constructor(
    private val repository: CategoriaRepository
) {
    suspend operator fun invoke(categoria: Categoria): Result<Long>{
        val listaActual = repository.obtenerCategoriasConConteo().first().map { it.nombre }
        val nombreResult = validarNombreCategoria(categoria.nombre, listaActual)
        if (!nombreResult.isValid){
            return Result.failure(IllegalArgumentException(nombreResult.error))
        }

        return runCatching { repository.guardarCategoria(categoria) }
    }
}