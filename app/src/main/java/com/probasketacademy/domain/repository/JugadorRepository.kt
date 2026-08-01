package com.probasketacademy.domain.repository

import com.probasketacademy.domain.model.Jugador
import kotlinx.coroutines.flow.Flow

interface JugadorRepository {
    suspend fun guardarJugador(jugador: Jugador)
    fun obtenerJugadorPorId(id: Long): Flow<Jugador?>
    fun obtenerJugadores(): Flow<List<Jugador>>
    fun obtenerJugadoresPorCategoria(categoriaId: Long): Flow<List<Jugador>>
    fun buscarJugadores(query: String): Flow<List<Jugador>>
    suspend fun eliminarJugador(jugadorId: Long)
}