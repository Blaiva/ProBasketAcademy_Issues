package com.probasketacademy.data.local.evento

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface EventoDao {

    @Upsert
    suspend fun guardarEvento(evento: EventoEntity)

    @Query("SELECT * FROM eventos WHERE fechaHoraEpocaMs BETWEEN :inicioDia AND :finDia ORDER BY fechaHoraEpocaMs ASC")
    fun obtenerEventosPorDia(inicioDia: Long, finDia: Long): Flow<List<EventoEntity>>

    @Query("DELETE FROM eventos WHERE id = :id")
    suspend fun eliminarEvento(id: Long)
}