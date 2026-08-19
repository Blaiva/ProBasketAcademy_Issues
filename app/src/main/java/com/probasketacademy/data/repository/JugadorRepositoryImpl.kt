package com.probasketacademy.data.repository

import com.probasketacademy.data.local.jugador.JugadorDao
import com.probasketacademy.data.mapper.toDomain
import com.probasketacademy.data.mapper.toEntity
import com.probasketacademy.domain.model.Jugador
import com.probasketacademy.domain.repository.JugadorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class JugadorRepositoryImpl @Inject constructor(
    private val jugadorDao: JugadorDao,
    private val userSession: UserSessionProvider
) : JugadorRepository {

    override suspend fun guardarJugador(jugador: Jugador) {
        jugadorDao.guardarJugador(jugador.toEntity(userSession.currentUserId))
    }

    override fun obtenerJugadorPorId(id: Long): Flow<Jugador?> {
        return userSession.observeUserId().flatMapLatest { userId ->
            jugadorDao.obtenerJugadorConCategoriaPorId(id, userId).map { it?.toDomain() }
        }
    }

    override fun obtenerJugadores(): Flow<List<Jugador>> {
        return userSession.observeUserId().flatMapLatest { userId ->
            jugadorDao.obtenerJugadoresConCategoria(userId).map { lista -> lista.map { it.toDomain() } }
        }
    }

    override fun obtenerJugadoresPorCategoria(categoriaId: Long): Flow<List<Jugador>> {
        return userSession.observeUserId().flatMapLatest { userId ->
            jugadorDao.obtenerJugadoresPorCategoria(categoriaId, userId).map { lista -> lista.map { it.toDomain() } }
        }
    }

    override fun buscarJugadores(query: String): Flow<List<Jugador>> {
        return userSession.observeUserId().flatMapLatest { userId ->
            jugadorDao.buscarJugadoresConCategoria(query, userId).map { lista -> lista.map { it.toDomain() } }
        }
    }

    override suspend fun eliminarJugador(jugadorId: Long) {
        jugadorDao.eliminarJugadorPorId(jugadorId, userSession.currentUserId)
    }
}