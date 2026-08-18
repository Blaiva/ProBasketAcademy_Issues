package com.probasketacademy.domain.usecase.pago

import com.probasketacademy.domain.model.Pago
import com.probasketacademy.domain.repository.PagoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObtenerPagosPorJugadorUseCase @Inject constructor(
    private val repository: PagoRepository
) {
    operator fun invoke(jugadorId: Long): Flow<List<Pago>> {
        return repository.obtenerPagosPorJugador(jugadorId)
    }
}