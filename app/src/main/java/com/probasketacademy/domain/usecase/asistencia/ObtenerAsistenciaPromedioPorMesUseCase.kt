package com.probasketacademy.domain.usecase.asistencia

import com.probasketacademy.domain.repository.AsistenciaRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObtenerAsistenciaPromedioPorMesUseCase @Inject constructor(
    private val repository: AsistenciaRepository
) {
    operator fun invoke(inicio: Long, fin: Long): Flow<Double?> {
        return repository.obtenerAsistenciaPromedioPorMes(inicio, fin)
    }
}