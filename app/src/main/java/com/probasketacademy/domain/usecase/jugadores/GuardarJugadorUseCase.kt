package com.probasketacademy.domain.usecase.jugadores

import com.probasketacademy.domain.model.Jugador
import com.probasketacademy.domain.repository.JugadorRepository
import kotlinx.coroutines.flow.firstOrNull
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class GuardarJugadorUseCase @Inject constructor(
    private val repository: JugadorRepository
) {
    suspend operator fun invoke(jugador: Jugador): Result<Unit> {
        val nombreVal = validarNombreJugador(jugador.nombre)
        val telefonoVal = validarTelefonoJugador(jugador.telefono)
        val edadVal = validarEdadJugador(jugador.edad.toString())
        val domicilioVal = validarDomicilioJugador(jugador.domicilio)
        val tallaVal = validarTallaCamisetaJugador(jugador.tallaCamiseta)
        val numeroVal = validarNumeroCamisetaJugador(jugador.numeroCamiseta.toString())
        val estaturaVal = validarEstaturaJugador(jugador.estatura.toString())
        val pesoVal = validarPesoJugador(jugador.peso.toString())
        val tutorNombreVal = validarTutorNombreJugador(jugador.tutorNombre)
        val tutorTelefonoVal = validarTutorTelefonoJugador(jugador.tutorTelefono)
        val tutorVinculoVal = validarTutorVinculoJugador(jugador.tutorVinculo)
        val tutorCorreoVal = validarTutorCorreoJugador(jugador.tutorCorreo)
        val estadoVal = validarEstadoJugador(jugador.estado)

        if (!nombreVal.isValid || !telefonoVal.isValid || !edadVal.isValid ||
            !domicilioVal.isValid || !tallaVal.isValid || !numeroVal.isValid ||
            !estaturaVal.isValid || !pesoVal.isValid || !tutorNombreVal.isValid ||
            !tutorTelefonoVal.isValid || !tutorVinculoVal.isValid ||
            !tutorCorreoVal.isValid || !estadoVal.isValid
        ) {
            val primerError = listOfNotNull(
                nombreVal.error, telefonoVal.error, edadVal.error, domicilioVal.error,
                tallaVal.error, numeroVal.error, estaturaVal.error, pesoVal.error,
                tutorNombreVal.error, tutorTelefonoVal.error, tutorVinculoVal.error,
                tutorCorreoVal.error, estadoVal.error
            ).firstOrNull() ?: "Datos inválidos"

            return Result.failure(IllegalArgumentException(primerError))
        }

        var jugadorFinal = jugador

        // Si el jugador ya existe (ID != 0), recuperamos sus datos de finanzas para no borrarlos
        if (jugador.jugadorId != 0L) {
            val existente = repository.obtenerJugadorPorId(jugador.jugadorId).firstOrNull()
            if (existente != null) {
                jugadorFinal = jugador.copy(
                    totalGenerado = existente.totalGenerado,
                    totalPagado = existente.totalPagado,
                    deudaActual = existente.deudaActual,
                    tipoInscripcion = existente.tipoInscripcion,
                    fechaInicio = existente.fechaInicio,
                    fechaVencimiento = existente.fechaVencimiento,
                    cuota = existente.cuota
                )
            }
        }

        // Lógica de fechas (solo si no existen, por ejemplo en un nuevo jugador)
        if (jugadorFinal.fechaInicio.isBlank()) {
            val hoy = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

            val fechaVencimiento = if (jugadorFinal.tipoInscripcion.equals("Semanal", ignoreCase = true)) {
                hoy.plusDays(7)
            } else {
                hoy.plusMonths(1)
            }

            jugadorFinal = jugadorFinal.copy(
                fechaInicio = hoy.format(formatter),
                fechaVencimiento = fechaVencimiento.format(formatter)
            )
        }

        return runCatching { repository.guardarJugador(jugadorFinal) }
    }
}