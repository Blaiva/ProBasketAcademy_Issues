package com.probasketacademy.data.repository

import com.probasketacademy.data.local.jugador.JugadorDao
import com.probasketacademy.data.mapper.toDomain
import com.probasketacademy.data.mapper.toEntity
import com.probasketacademy.domain.model.Jugador
import com.probasketacademy.domain.repository.JugadorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class JugadorRepositoryImpl @Inject constructor(private val jugadorDao: JugadorDao): JugadorRepository {
    override fun obtenerJugadoresConCategoria(query: String): Flow<List<Jugador>> {
        return  jugadorDao.obtenerJugadoresConCategoria(query).map { lista -> lista.map { it.toDomain() } }
    }

    override suspend fun obtenerJugadorPorId(jugadorId: Long): Jugador? {
        return jugadorDao.obtenerJugadorPorId(jugadorId)?.toDomain()
    }

    override suspend fun guardarJugador(jugador: Jugador): Long {
        jugadorDao.guardarJugador(jugador.toEntity())
        return jugador.id ?: 0
    }

    override suspend fun eliminarJugadorPorId(jugadorId: Long) {
        jugadorDao.eliminarJugadorPorId(jugadorId)
    }

}