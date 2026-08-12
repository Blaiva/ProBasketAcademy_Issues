package com.probasketacademy.domain.usecase.jugadores

import com.probasketacademy.domain.model.Jugador
import com.probasketacademy.domain.repository.JugadorRepository
import javax.inject.Inject

class RemoverJugadorDeCategoriaUseCase @Inject constructor(
    private val jugadorRepository: JugadorRepository
) {
    suspend operator fun invoke(jugador: Jugador) {
        val jugadorSinCategoria = jugador.copy(
            categoriaId = null,
            categoriaNombre = ""
        )
        jugadorRepository.guardarJugador(jugadorSinCategoria)
    }
}