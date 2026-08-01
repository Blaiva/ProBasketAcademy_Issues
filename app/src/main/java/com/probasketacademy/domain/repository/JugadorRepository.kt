package com.probasketacademy.domain.repository

import com.probasketacademy.domain.model.Jugador
import kotlinx.coroutines.flow.Flow

interface JugadorRepository {
    fun obtenerJugadoresConCategoria(query: String = ""): Flow<List<Jugador>>
    suspend fun obtenerJugadorPorId(jugadorId: Long): Jugador?
    suspend fun guardarJugador(jugador: Jugador): Long
    suspend fun eliminarJugadorPorId(jugadorId: Long)
}