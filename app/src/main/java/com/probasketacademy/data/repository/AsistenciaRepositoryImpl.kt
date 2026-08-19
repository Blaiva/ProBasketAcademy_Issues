package com.probasketacademy.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.probasketacademy.data.local.asistencia.AsistenciaDao
import com.probasketacademy.data.mapper.toDomain
import com.probasketacademy.data.mapper.toEntity
import com.probasketacademy.domain.model.Asistencia
import com.probasketacademy.domain.repository.AsistenciaRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AsistenciaRepositoryImpl @Inject constructor(
    private val asistenciaDao: AsistenciaDao,
    private val userSession: UserSessionProvider
): AsistenciaRepository {

    override fun obtenerListaAsistenciaPorCategoria(categoriaId: Long, fechaTimestamp: Long): Flow<List<Asistencia>> {
        return userSession.observeUserId().flatMapLatest { userId ->
            asistenciaDao.obtenerListaAsistenciaPorCategoria(categoriaId, fechaTimestamp, userId)
                .map { lista -> lista.map { it.toDomain(categoriaId, fechaTimestamp) } }
        }
    }

    override suspend fun registrarAsistencias(asistencias: List<Asistencia>): List<Long> {
        asistenciaDao.registrarAsistencias(asistencias.map { it.toEntity(userSession.currentUserId) })
        return asistencias.map { it.id }
    }

    override fun obtenerAsistenciasPorDia(fechaTimestamp: Long): Flow<List<Asistencia>> {
        return userSession.observeUserId().flatMapLatest { userId ->
            asistenciaDao.obtenerAsistenciasPorDia(fechaTimestamp, userId).map { lista -> lista.map { it.toDomain() } }
        }
    }

    override fun obtenerAsistenciaPromedioPorMes(inicio: Long, fin: Long): Flow<Double?> {
        return userSession.observeUserId().flatMapLatest { userId ->
            asistenciaDao.obtenerAsistenciaPromedioPorMes(inicio, fin, userId)
        }
    }

    override suspend fun obtenerAsistenciaExistente(jugadorId: Long, fechaTimestamp: Long): Asistencia? {
        return asistenciaDao.obtenerAsistenciaPorJugadorYFecha(jugadorId, fechaTimestamp, userSession.currentUserId)?.toDomain()
    }
}