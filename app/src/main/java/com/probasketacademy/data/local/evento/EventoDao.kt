package com.probasketacademy.data.local.evento

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface EventoDao {
    @Upsert
    suspend fun guardarEvento(evento: EventoEntity)

    @Query("SELECT * FROM eventos WHERE fechaHoraEpocaMs BETWEEN :inicioDia AND :finDia AND userId = :userId ORDER BY fechaHoraEpocaMs ASC")
    fun obtenerEventosPorDia(inicioDia: Long, finDia: Long, userId: String): Flow<List<EventoEntity>>

    @Query("DELETE FROM eventos WHERE id = :id AND userId = :userId")
    suspend fun eliminarEvento(id: Long, userId: String)
}