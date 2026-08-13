package com.probasketacademy.data.repository

import com.probasketacademy.data.local.pago.PagoDao
import com.probasketacademy.data.mapper.toDomain
import com.probasketacademy.data.mapper.toEntity
import com.probasketacademy.domain.model.Pago
import com.probasketacademy.domain.repository.PagoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PagoRepositoryImpl @Inject constructor(private val pagoDao: PagoDao): PagoRepository {
    override fun obtenerPagosPorJugador(jugadorId: Long): Flow<List<Pago>> {
        return pagoDao.obtenerPagosPorJugador(jugadorId).map { lista -> lista.map { it.toDomain() } }
    }

    override fun obtenerCobrosPendientes(): Flow<List<Pago>> {
        return pagoDao.obtenerCobrosPendientes().map { lista -> lista.map { it.toDomain() } }
    }

    override suspend fun registrarPago(pago: Pago): Long {
        pagoDao.registrarPago(pago.toEntity())
        return pago.id ?: 0
    }

    override fun obtenerIngresosTotales(): Flow<Double?> {
        return pagoDao.obtenerIngresosTotales()
    }
}