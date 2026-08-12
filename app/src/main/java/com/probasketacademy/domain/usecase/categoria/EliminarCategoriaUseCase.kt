package com.probasketacademy.domain.usecase.categoria

import com.probasketacademy.domain.repository.CategoriaRepository
import com.probasketacademy.domain.repository.JugadorRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class EliminarCategoriaUseCase @Inject constructor(
    private val categoriaRepository: CategoriaRepository,
    private val jugadorRepository: JugadorRepository
) {
    suspend operator fun invoke(categoriaId: Long) {
        val jugadores = jugadorRepository.obtenerJugadoresPorCategoria(categoriaId).first()
        jugadores.forEach { jugador ->
            jugadorRepository.guardarJugador(jugador.copy(categoriaId = null, categoriaNombre = ""))
        }
        categoriaRepository.eliminarCategoria(categoriaId)
    }
}