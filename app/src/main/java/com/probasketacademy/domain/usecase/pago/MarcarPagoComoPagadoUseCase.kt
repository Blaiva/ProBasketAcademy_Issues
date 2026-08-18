package com.probasketacademy.domain.usecase.pago

import com.probasketacademy.domain.model.Jugador
import com.probasketacademy.domain.model.Pago
import com.probasketacademy.domain.repository.JugadorRepository
import com.probasketacademy.domain.repository.PagoRepository
import javax.inject.Inject

class MarcarPagoComoPagadoUseCase @Inject constructor(
    private val pagoRepository: PagoRepository,
    private val jugadorRepository: JugadorRepository
) {
    suspend operator fun invoke(pago: Pago, jugador: Jugador): Result<Unit> {
        if (pago.deuda <= 0.0) {
            return Result.failure(IllegalArgumentException("Este cobro ya está saldado"))
        }

        val abonoRestante = pago.deuda

        val pagoActualizado = pago.copy(
            montoPagado = pago.montoTotal,
            deuda = 0.0,
            estado = "PAGADO"
        )

        return runCatching {
            pagoRepository.registrarPago(pagoActualizado)

            val nuevaDeuda = (jugador.deudaActual - abonoRestante).coerceAtLeast(0.0)
            val jugadorActualizado = jugador.copy(
                totalPagado = jugador.totalPagado + abonoRestante,
                deudaActual = nuevaDeuda
            )
            jugadorRepository.guardarJugador(jugadorActualizado)
        }
    }
}