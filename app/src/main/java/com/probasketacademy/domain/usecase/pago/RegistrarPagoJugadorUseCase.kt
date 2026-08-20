package com.probasketacademy.domain.usecase.pago

import com.probasketacademy.domain.model.Jugador
import com.probasketacademy.domain.model.Pago
import com.probasketacademy.domain.repository.JugadorRepository
import com.probasketacademy.domain.repository.PagoRepository
import javax.inject.Inject

data class DatosPagoJugador(
    val jugador: Jugador,
    val concepto: String,
    val montoTotal: Double,
    val montoAbonado: Double,
    val fecha: String,
    val tipoInscripcion: String,
    val fechaInicio: String,
    val fechaVencimiento: String
)

class RegistrarPagoJugadorUseCase @Inject constructor(
    private val pagoRepository: PagoRepository,
    private val jugadorRepository: JugadorRepository
) {
    suspend operator fun invoke(datos: DatosPagoJugador): Result<Unit> {
        if (datos.montoTotal <= 0.0) {
            return Result.failure(IllegalArgumentException("El monto total debe ser mayor a 0"))
        }
        if (datos.montoAbonado < 0.0) {
            return Result.failure(IllegalArgumentException("El monto abonado no puede ser negativo"))
        }

        val deuda = (datos.montoTotal - datos.montoAbonado).coerceAtLeast(0.0)
        val estado = when {
            deuda <= 0.0 -> "PAGADO"
            datos.montoAbonado > 0.0 -> "ABONADO"
            else -> "PENDIENTE"
        }

        val nuevoPago = Pago(
            jugadorId = datos.jugador.jugadorId,
            concepto = datos.concepto.ifBlank { "Cuota Academia" },
            montoTotal = datos.montoTotal,
            montoPagado = datos.montoAbonado,
            deuda = deuda,
            fecha = datos.fecha,
            estado = estado,
            jugadorNombre = datos.jugador.nombre,
            numeroCamiseta = datos.jugador.numeroCamiseta
        )

        return runCatching {
            pagoRepository.registrarPago(nuevoPago)

            val jugadorActualizado = datos.jugador.copy(
                totalGenerado = datos.jugador.totalGenerado + datos.montoTotal,
                totalPagado = datos.jugador.totalPagado + datos.montoAbonado,
                deudaActual = datos.jugador.deudaActual + deuda,
                tipoInscripcion = datos.tipoInscripcion,
                fechaInicio = datos.fechaInicio,
                fechaVencimiento = datos.fechaVencimiento,
                cuota = datos.montoTotal
            )
            jugadorRepository.guardarJugador(jugadorActualizado)
        }
    }
}