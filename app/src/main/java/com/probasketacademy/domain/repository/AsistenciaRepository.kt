package com.probasketacademy.domain.repository

import com.probasketacademy.domain.model.Asistencia

interface AsistenciaRepository {
    fun obtenerListaAsistenciaPorCategoria(categoriaId: Long, fechaTimestamp: Long)
    suspend fun registrarAsistencias(asistencias: List<Asistencia>): List<Long>
}