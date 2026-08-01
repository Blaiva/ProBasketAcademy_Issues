package com.probasketacademy.domain.repository

import com.probasketacademy.domain.model.Asistencia
import kotlinx.coroutines.flow.Flow

interface AsistenciaRepository {
    fun obtenerListaAsistenciaPorCategoria(categoriaId: Long, fechaTimestamp: Long): Flow<List<Asistencia>>
    suspend fun registrarAsistencias(asistencias: List<Asistencia>): List<Long>
}