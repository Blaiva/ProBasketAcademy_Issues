package com.probasketacademy.data.repository

import com.probasketacademy.data.local.evento.EventoDao
import com.probasketacademy.data.mapper.toDomain
import com.probasketacademy.data.mapper.toEntity
import com.probasketacademy.domain.model.Evento
import com.probasketacademy.domain.repository.EventoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class EventoRepositoryImpl @Inject constructor(private val eventoDao: EventoDao): EventoRepository {
    override fun obtenerEventosPorDia(
        inicioDia: Long,
        finDia: Long
    ): Flow<List<Evento>> {
        return eventoDao.obtenerEventosPorDia(inicioDia, finDia).map { lista -> lista.map { it.toDomain() } }
    }

    override suspend fun guardarEvento(evento: Evento): Long {
        eventoDao.guardarEvento(evento.toEntity())
        return evento.id
    }

    override suspend fun eliminarEvento(id: Long) {
        eventoDao.eliminarEvento(id)
    }
}