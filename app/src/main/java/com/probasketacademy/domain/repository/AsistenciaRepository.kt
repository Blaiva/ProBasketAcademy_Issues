package com.probasketacademy.domain.repository

import com.probasketacademy.domain.model.Asistencia
import kotlinx.coroutines.flow.Flow

interface AsistenciaRepository {
    fun obtenerListaAsistenciaPorCategoria(categoriaId: Long, fechaTimestamp: Long): Flow<List<Asistencia>>
    suspend fun registrarAsistencias(asistencias: List<Asistencia>): List<Long>
    fun obtenerAsistenciasPorDia(fechaTimestamp: Long): Flow<List<Asistencia>>
    fun obtenerAsistenciaPromedioPorMes(inicio: Long, fin: Long): Flow<Double?>
    suspend fun obtenerAsistenciaExistente(jugadorId: Long, fechaTimestamp: Long): Asistencia?
}