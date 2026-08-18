package com.probasketacademy.domain.repository

import com.probasketacademy.domain.model.Evento
import kotlinx.coroutines.flow.Flow

interface EventoRepository {
    fun obtenerEventosPorDia(inicioDia: Long, finDia: Long): Flow<List<Evento>>
    suspend fun guardarEvento(evento: Evento): Long
    suspend fun eliminarEvento(id: Long)
}