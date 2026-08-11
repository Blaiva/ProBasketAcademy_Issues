package com.probasketacademy.domain.usecase.jugadores

import com.probasketacademy.domain.repository.JugadorRepository
import javax.inject.Inject

class EliminarJugadorUseCase @Inject constructor(
    private val repository: JugadorRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.eliminarJugador(id)
    }
}