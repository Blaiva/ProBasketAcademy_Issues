package com.probasketacademy.domain.usecase.jugadores

import com.probasketacademy.domain.model.Jugador
import com.probasketacademy.domain.repository.JugadorRepository
import javax.inject.Inject

class AsignarJugadoresACategoriaUseCase @Inject constructor(
    private val jugadorRepository: JugadorRepository
) {
    suspend operator fun invoke(jugadores: List<Jugador>, categoriaId: Long, categoriaNombre: String) {
        jugadores.forEach { jugador ->
            jugadorRepository.guardarJugador(
                jugador.copy(
                    categoriaId = categoriaId,
                    categoriaNombre = categoriaNombre
                )
            )
        }
    }
}