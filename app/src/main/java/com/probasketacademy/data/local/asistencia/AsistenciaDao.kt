package com.probasketacademy.data.local.asistencia

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface AsistenciaDao {
    @Upsert
    suspend fun registrarAsistencias(asistencias: List<AsistenciaEntity>)

    @Query("""
        SELECT 
            j.id AS jugadorId,
            j.nombre AS nombreJugador,
            j.fotoUri AS fotoUri,
            a.asistio AS asistio
        FROM jugadores j
        LEFT JOIN asistencias a ON j.id = a.jugadorId AND a.fechaEpocaMs = :fechaTimestamp
        WHERE j.categoriaId = :categoriaId AND j.estaActivo = 1
        ORDER BY j.nombre ASC
    """)
    fun obtenerListaAsistenciaPorCategoria(categoriaId: Long, fechaTimestamp: Long): Flow<List<AsistenciaJugadorDto>>
}
