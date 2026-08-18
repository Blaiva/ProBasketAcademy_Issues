package com.probasketacademy.domain.usecase.pago

import com.probasketacademy.domain.model.Jugador
import com.probasketacademy.domain.model.Pago
import com.probasketacademy.domain.repository.JugadorRepository
import com.probasketacademy.domain.repository.PagoRepository
import javax.inject.Inject

class RegistrarPagoJugadorUseCase @Inject constructor(
    private val pagoRepository: PagoRepository,
    private val jugadorRepository: JugadorRepository
) {
    suspend operator fun invoke(
        jugador: Jugador,
        concepto: String,
        montoTotal: Double,
        montoAbonado: Double,
        fecha: String,
        tipoInscripcion: String,
        fechaInicio: String,
        fechaVencimiento: String
    ): Result<Unit> {
        if (montoTotal <= 0.0) {
            return Result.failure(IllegalArgumentException("El monto total debe ser mayor a 0"))
        }
        if (montoAbonado < 0.0) {
            return Result.failure(IllegalArgumentException("El monto abonado no puede ser negativo"))
        }

        val deuda = (montoTotal - montoAbonado).coerceAtLeast(0.0)
        val estado = when {
            deuda <= 0.0 -> "PAGADO"
            montoAbonado > 0.0 -> "ABONADO"
            else -> "PENDIENTE"
        }

        val nuevoPago = Pago(
            jugadorId = jugador.jugadorId,
            concepto = concepto.ifBlank { "Cuota Academia" },
            montoTotal = montoTotal,
            montoPagado = montoAbonado,
            deuda = deuda,
            fecha = fecha,
            estado = estado,
            jugadorNombre = jugador.nombre,
            numeroCamiseta = jugador.numeroCamiseta
        )

        return runCatching {
            pagoRepository.registrarPago(nuevoPago)

            val jugadorActualizado = jugador.copy(
                totalGenerado = jugador.totalGenerado + montoTotal,
                totalPagado = jugador.totalPagado + montoAbonado,
                deudaActual = jugador.deudaActual + deuda,
                tipoInscripcion = tipoInscripcion,
                fechaInicio = fechaInicio,
                fechaVencimiento = fechaVencimiento,
                cuota = montoTotal
            )
            jugadorRepository.guardarJugador(jugadorActualizado)
        }
    }
}