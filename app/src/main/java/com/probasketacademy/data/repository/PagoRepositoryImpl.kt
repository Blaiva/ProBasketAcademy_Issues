package com.probasketacademy.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.probasketacademy.data.local.pago.PagoDao
import com.probasketacademy.data.mapper.toDomain
import com.probasketacademy.data.mapper.toEntity
import com.probasketacademy.domain.model.Pago
import com.probasketacademy.domain.repository.PagoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PagoRepositoryImpl @Inject constructor(
    private val pagoDao: PagoDao,
    private val userSession: UserSessionProvider
): PagoRepository {

    override fun obtenerPagosPorJugador(jugadorId: Long): Flow<List<Pago>> {
        return userSession.observeUserId().flatMapLatest { userId ->
            pagoDao.obtenerPagosPorJugador(jugadorId, userId).map { lista -> lista.map { it.toDomain() } }
        }
    }

    override fun obtenerCobrosPendientes(): Flow<List<Pago>> {
        return userSession.observeUserId().flatMapLatest { userId ->
            pagoDao.obtenerCobrosPendientes(userId).map { lista -> lista.map { it.toDomain() } }
        }
    }

    override suspend fun registrarPago(pago: Pago): Long {
        pagoDao.registrarPago(pago.toEntity(userSession.currentUserId))
        return pago.id
    }

    override fun obtenerIngresosTotales(): Flow<Double?> {
        return userSession.observeUserId().flatMapLatest { userId ->
            pagoDao.obtenerIngresosTotales(userId)
        }
    }
}