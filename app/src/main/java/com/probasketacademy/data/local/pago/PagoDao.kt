package com.probasketacademy.data.local.pago

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PagoDao {
    @Upsert
    suspend fun registrarPago(pago: PagoEntity)

    @Query("""
        SELECT 
            p.id AS pagoId,
            p.jugadorId AS jugadorId,
            p.concepto AS concepto,
            p.monto AS monto,
            p.fecha AS fecha,
            p.estado AS estado,
            j.nombre AS jugadorNombre,
            j.numeroCamiseta AS numeroCamiseta
        FROM pagos p
        INNER JOIN jugadores j ON p.jugadorId = j.jugadorId
        WHERE p.jugadorId = :jugadorId
        ORDER BY p.id DESC
    """)
    fun obtenerPagosPorJugador(jugadorId: Long): Flow<List<PagoConJugadorDto>>

    // Consulta explícita para cobros pendientes mostrados en el Dashboard
    @Query("""
        SELECT 
            p.id AS pagoId,
            p.jugadorId AS jugadorId,
            p.concepto AS concepto,
            p.monto AS monto,
            p.fecha AS fecha,
            p.estado AS estado,
            j.nombre AS jugadorNombre,
            j.numeroCamiseta AS numeroCamiseta
        FROM pagos p
        INNER JOIN jugadores j ON p.jugadorId = j.jugadorId
        WHERE p.estado = 'PENDIENTE'
        ORDER BY p.id DESC
    """)
    fun obtenerCobrosPendientes(): Flow<List<PagoConJugadorDto>>

    @Query("""
        SELECT SUM(monto) 
        FROM pagos 
        WHERE estado = 'PAGADO'
    """)
    fun obtenerIngresosTotales(): Flow<Double?>
}