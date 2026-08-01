package com.probasketacademy.data.repository

import com.probasketacademy.data.local.jugador.JugadorDao
import com.probasketacademy.data.mapper.toDomain
import com.probasketacademy.data.mapper.toEntity
import com.probasketacademy.domain.model.Jugador
import com.probasketacademy.domain.repository.JugadorRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class JugadorRepositoryImpl @Inject constructor(
    private val jugadorDao: JugadorDao
) : JugadorRepository {

    override suspend fun guardarJugador(jugador: Jugador) {
        jugadorDao.guardarJugador(jugador.toEntity())
    }

    override fun obtenerJugadorPorId(id: Long): Flow<Jugador?> {
        return jugadorDao.obtenerJugadorConCategoriaPorId(id).map { dto ->
            dto?.toDomain()
        }
    }

    override fun obtenerJugadores(): Flow<List<Jugador>> {
        return jugadorDao.obtenerJugadoresConCategoria().map { lista ->
            lista.map { it.toDomain() }
        }
    }

    override fun obtenerJugadoresPorCategoria(categoriaId: Long): Flow<List<Jugador>> {
        return jugadorDao.obtenerJugadoresPorCategoria(categoriaId).map { lista ->
            lista.map { it.toDomain() }
        }
    }

    override fun buscarJugadores(query: String): Flow<List<Jugador>> {
        return jugadorDao.buscarJugadoresConCategoria(query).map { lista ->
            lista.map { it.toDomain() }
        }
    }

    override suspend fun eliminarJugador(jugadorId: Long) {
        jugadorDao.eliminarJugadorPorId(jugadorId)
    }
}