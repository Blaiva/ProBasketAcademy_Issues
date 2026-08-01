package com.probasketacademy.data.local.jugador

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface JugadorDao {
    @Upsert
    suspend fun guardarJugador(entity: JugadorEntity): Long

    @Query("DELETE FROM jugadores WHERE id = :jugadorId")
    suspend fun eliminarJugadorPorId(jugadorId: Long)

    @Query("""
        SELECT 
            j.id AS id,
            j.nombre AS nombre,
            j.posicion AS posicion,
            j.estaActivo AS estaActivo,
            j.docCompleta AS docCompleta,
            j.fotoUri AS fotoUri,
            c.nombre AS categoriaNombre
        FROM jugadores j
        INNER JOIN categorias c ON j.categoriaId = c.id
        WHERE j.nombre LIKE '%' || :query || '%' OR j.posicion LIKE '%' || :query || '%'
        ORDER BY j.nombre ASC
    """)
    fun obtenerJugadoresConCategoria(query: String = ""): Flow<List<JugadorConCategoriaDto>>

    @Query("SELECT * FROM jugadores WHERE id = :jugadorId")
    suspend fun obtenerJugadorPorId(jugadorId: Long): JugadorEntity?
}