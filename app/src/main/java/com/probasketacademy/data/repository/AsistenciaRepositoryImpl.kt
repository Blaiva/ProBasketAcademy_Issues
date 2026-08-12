package com.probasketacademy.data.repository

import com.probasketacademy.data.local.asistencia.AsistenciaDao
import com.probasketacademy.data.mapper.toDomain
import com.probasketacademy.data.mapper.toEntity
import com.probasketacademy.domain.model.Asistencia
import com.probasketacademy.domain.repository.AsistenciaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AsistenciaRepositoryImpl @Inject constructor(private val asistenciaDao: AsistenciaDao): AsistenciaRepository {
    override fun obtenerListaAsistenciaPorCategoria(categoriaId: Long, fechaTimestamp: Long): Flow<List<Asistencia>> {
        return asistenciaDao.obtenerListaAsistenciaPorCategoria(categoriaId, fechaTimestamp).map { lista -> lista.map { it.toDomain(categoriaId, fechaTimestamp) } }
    }

    override suspend fun registrarAsistencias(asistencias: List<Asistencia>): List<Long> {
        asistenciaDao.registrarAsistencias(asistencias.map { it.toEntity() })
        return asistencias.map { it.id ?: 0 }
    }

    override fun obtenerAsistenciasPorDia(fechaTimestamp: Long): Flow<List<Asistencia>> {
        return asistenciaDao.obtenerAsistenciasPorDia(fechaTimestamp).map { lista -> lista.map { it.toDomain() } }
    }

    override fun obtenerAsistenciaPromedioPorMes(inicio: Long, fin: Long): Flow<Double?> {
        return asistenciaDao.obtenerAsistenciaPromedioPorMes(inicio, fin)
    }
}