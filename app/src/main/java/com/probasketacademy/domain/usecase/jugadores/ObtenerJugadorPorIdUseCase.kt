package com.probasketacademy.domain.usecase.jugadores

import com.probasketacademy.domain.model.Jugador
import com.probasketacademy.domain.repository.JugadorRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObtenerJugadorPorIdUseCase @Inject constructor(
    private val repository: JugadorRepository
) {
    operator fun invoke(id: Long): Flow<Jugador?> {
        return repository.obtenerJugadorPorId(id)
    }
}