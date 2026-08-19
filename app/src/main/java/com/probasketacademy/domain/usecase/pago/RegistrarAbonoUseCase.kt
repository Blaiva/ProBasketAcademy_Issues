package com.probasketacademy.domain.usecase.pago

import com.probasketacademy.domain.model.Jugador
import com.probasketacademy.domain.model.Pago
import com.probasketacademy.domain.repository.JugadorRepository
import com.probasketacademy.domain.repository.PagoRepository
import javax.inject.Inject

class RegistrarAbonoUseCase @Inject constructor(
    private val pagoRepository: PagoRepository,
    private val jugadorRepository: JugadorRepository
) {
    suspend operator fun invoke(
        pago: Pago,
        jugador: Jugador,
        montoAbono: Double
    ): Result<Unit> {
        if (montoAbono <= 0.0) {
            return Result.failure(IllegalArgumentException("El monto del abono debe ser mayor a 0"))
        }

        if (montoAbono > pago.deuda) {
            return Result.failure(
                IllegalArgumentException("El abono no puede ser mayor a la deuda pendiente ($${pago.deuda})")
            )
        }

        val nuevoMontoPagado = pago.montoPagado + montoAbono
        val nuevaDeuda = (pago.montoTotal - nuevoMontoPagado).coerceAtLeast(0.0)
        val nuevoEstado = if (nuevaDeuda <= 0.0) "PAGADO" else "ABONADO"

        val pagoActualizado = pago.copy(
            montoPagado = nuevoMontoPagado,
            deuda = nuevaDeuda,
            estado = nuevoEstado
        )

        return runCatching {
            pagoRepository.registrarPago(pagoActualizado)

            val jugadorActualizado = jugador.copy(
                totalPagado = jugador.totalPagado + montoAbono,
                deudaActual = (jugador.deudaActual - montoAbono).coerceAtLeast(0.0)
            )
            jugadorRepository.guardarJugador(jugadorActualizado)
        }
    }
}