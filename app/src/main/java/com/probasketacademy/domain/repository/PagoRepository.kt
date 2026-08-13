package com.probasketacademy.domain.repository

import com.probasketacademy.domain.model.Pago
import kotlinx.coroutines.flow.Flow

interface PagoRepository {
    fun obtenerPagosPorJugador(jugadorId: Long): Flow<List<Pago>>
    fun obtenerCobrosPendientes(): Flow<List<Pago>>
    suspend fun registrarPago(pago: Pago): Long

    fun obtenerIngresosTotales(): Flow<Double?>
}