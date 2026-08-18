package com.probasketacademy.domain.usecase.asistencia

import com.probasketacademy.domain.model.Asistencia
import com.probasketacademy.domain.repository.AsistenciaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObtenerAsistenciasPorDiaUseCase @Inject constructor(
    private val repository: AsistenciaRepository
) {
    operator fun invoke(fechaTimestamp: Long): Flow<List<Asistencia>> {
        return repository.obtenerAsistenciasPorDia(fechaTimestamp)
    }
}