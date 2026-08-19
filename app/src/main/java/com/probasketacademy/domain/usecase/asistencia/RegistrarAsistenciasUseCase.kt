package com.probasketacademy.domain.usecase.asistencia

import com.probasketacademy.domain.model.Asistencia
import com.probasketacademy.domain.repository.AsistenciaRepository
import javax.inject.Inject

class RegistrarAsistenciasUseCase @Inject constructor(
    private val repository: AsistenciaRepository
) {
    suspend operator fun invoke(asistencias: List<Asistencia>): Result<List<Long>> {
        if (asistencias.isEmpty()) {
            return Result.failure(IllegalArgumentException("No hay asistencias para registrar"))
        }
        return runCatching { repository.registrarAsistencias(asistencias) }
    }
}