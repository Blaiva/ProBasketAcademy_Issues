package com.probasketacademy.domain.usecase.jugadores

import com.probasketacademy.domain.model.Jugador
import com.probasketacademy.domain.repository.JugadorRepository
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

        return runCatching { repository.guardarJugador(jugador) }
    }
}