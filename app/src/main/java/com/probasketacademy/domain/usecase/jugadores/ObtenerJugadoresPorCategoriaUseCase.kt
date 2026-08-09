package com.probasketacademy.domain.usecase.jugadores

import com.probasketacademy.domain.model.Jugador
import com.probasketacademy.domain.repository.JugadorRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObtenerJugadoresPorCategoriaUseCase @Inject constructor(
    private val jugadorRepository: JugadorRepository
) {
    operator fun invoke(categoriaId: Long): Flow<List<Jugador>> {
        return jugadorRepository.obtenerJugadoresPorCategoria(categoriaId)
    }
}