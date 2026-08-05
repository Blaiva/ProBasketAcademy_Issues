package com.probasketacademy.data.local.jugador

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface JugadorDao {

    @Upsert
    suspend fun guardarJugador(jugador: JugadorEntity)

    @Query("""
        SELECT j.*, c.nombre AS categoriaNombre
        FROM jugadores j
        INNER JOIN categorias c ON j.categoriaId = c.id
        WHERE j.jugadorId = :id
    """)
    fun obtenerJugadorConCategoriaPorId(id: Long): Flow<JugadorConCategoriaDto?>

    @Query("""
        SELECT j.*, c.nombre AS categoriaNombre
        FROM jugadores j
        INNER JOIN categorias c ON j.categoriaId = c.id
        ORDER BY j.nombre ASC
    """)
    fun obtenerJugadoresConCategoria(): Flow<List<JugadorConCategoriaDto>>

    @Query("""
        SELECT j.*, c.nombre AS categoriaNombre
        FROM jugadores j
        INNER JOIN categorias c ON j.categoriaId = c.id
        WHERE j.categoriaId = :categoriaId
        ORDER BY j.nombre ASC
    """)
    fun obtenerJugadoresPorCategoria(categoriaId: Long): Flow<List<JugadorConCategoriaDto>>

    @Query("""
        SELECT j.*, c.nombre AS categoriaNombre
        FROM jugadores j
        INNER JOIN categorias c ON j.categoriaId = c.id
        WHERE j.nombre LIKE '%' || :query || '%'
        ORDER BY j.nombre ASC
    """)
    fun buscarJugadoresConCategoria(query: String): Flow<List<JugadorConCategoriaDto>>

    @Query("DELETE FROM jugadores WHERE jugadorId = :jugadorId")
    suspend fun eliminarJugadorPorId(jugadorId: Long)
}