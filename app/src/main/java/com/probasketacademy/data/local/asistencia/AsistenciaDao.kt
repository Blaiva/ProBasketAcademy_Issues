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
            j.jugadorId AS jugadorId, j.nombre AS nombreJugador, j.fotoUri AS fotoUri, a.asistio AS asistio
        FROM jugadores j
        LEFT JOIN asistencias a ON j.jugadorId = a.jugadorId AND a.fechaEpocaMs = :fechaTimestamp AND a.userId = :userId
        WHERE j.categoriaId = :categoriaId AND j.estado = 'Activo' AND j.userId = :userId
        ORDER BY j.nombre ASC
    """)
    fun obtenerListaAsistenciaPorCategoria(categoriaId: Long, fechaTimestamp: Long, userId: String): Flow<List<AsistenciaJugadorDto>>

    @Query("SELECT * FROM asistencias WHERE fechaEpocaMs = :fechaTimestamp AND userId = :userId")
    fun obtenerAsistenciasPorDia(fechaTimestamp: Long, userId: String): Flow<List<AsistenciaEntity>>

    @Query("SELECT AVG(CAST(asistio AS INTEGER)) * 100 FROM asistencias WHERE fechaEpocaMs BETWEEN :inicio AND :fin AND userId = :userId")
    fun obtenerAsistenciaPromedioPorMes(inicio: Long, fin: Long, userId: String): Flow<Double?>

    @Query("SELECT * FROM asistencias WHERE jugadorId = :jugadorId AND fechaEpocaMs = :fechaTimestamp AND userId = :userId LIMIT 1")
    suspend fun obtenerAsistenciaPorJugadorYFecha(jugadorId: Long, fechaTimestamp: Long, userId: String): AsistenciaEntity?
}