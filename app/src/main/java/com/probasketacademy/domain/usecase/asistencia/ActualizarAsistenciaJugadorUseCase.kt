package com.probasketacademy.domain.usecase.asistencia

import com.probasketacademy.domain.model.Asistencia
import com.probasketacademy.domain.repository.AsistenciaRepository
import javax.inject.Inject

class ActualizarAsistenciaJugadorUseCase @Inject constructor(
    private val repository: AsistenciaRepository
) {
    suspend operator fun invoke(
        jugadorId: Long,
        categoriaId: Long,
        fechaTimestamp: Long,
        asistio: Boolean,
        nombreJugador: String
    ): Result<Unit> {
        return runCatching {
            val existente = repository.obtenerAsistenciaExistente(jugadorId, fechaTimestamp)
            val asistencia = Asistencia(
                id = existente?.id ?: 0L,
                jugadorId = jugadorId,
                categoriaId = categoriaId,
                fechaEpocaMs = fechaTimestamp,
                asistio = asistio,
                nombreJugador = nombreJugador
            )
            repository.registrarAsistencias(listOf(asistencia))
            Unit
        }
    }
}