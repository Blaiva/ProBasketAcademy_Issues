package com.probasketacademy.domain.usecase.jugadores

import com.probasketacademy.domain.model.Jugador
import com.probasketacademy.domain.repository.JugadorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObtenerJugadoresSinCategoriaUseCase @Inject constructor(
    private val jugadorRepository: JugadorRepository
) {
    operator fun invoke(): Flow<List<Jugador>> {
        return jugadorRepository.obtenerJugadores().map { lista ->
            lista.filter { it.categoriaId == null }
        }
    }
}