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
        SELECT j.*, COALESCE(c.nombre, 'Sin Categoría') AS categoriaNombre 
        FROM jugadores j 
        LEFT JOIN categorias c ON j.categoriaId = c.id 
        WHERE j.jugadorId = :id AND j.userId = :userId
    """)
    fun obtenerJugadorConCategoriaPorId(id: Long, userId: String): Flow<JugadorConCategoriaDto?>

    @Query("""
        SELECT j.*, COALESCE(c.nombre, 'Sin Categoría') AS categoriaNombre 
        FROM jugadores j 
        LEFT JOIN categorias c ON j.categoriaId = c.id
        WHERE j.userId = :userId
    """)
    fun obtenerJugadoresConCategoria(userId: String): Flow<List<JugadorConCategoriaDto>>

    @Query("""
        SELECT j.*, c.nombre AS categoriaNombre
        FROM jugadores j
        INNER JOIN categorias c ON j.categoriaId = c.id
        WHERE j.categoriaId = :categoriaId AND j.userId = :userId
        ORDER BY j.nombre ASC
    """)
    fun obtenerJugadoresPorCategoria(categoriaId: Long, userId: String): Flow<List<JugadorConCategoriaDto>>

    @Query("""
        SELECT j.*, c.nombre AS categoriaNombre
        FROM jugadores j
        INNER JOIN categorias c ON j.categoriaId = c.id
        WHERE j.nombre LIKE '%' || :query || '%' AND j.userId = :userId
        ORDER BY j.nombre ASC
    """)
    fun buscarJugadoresConCategoria(query: String, userId: String): Flow<List<JugadorConCategoriaDto>>

    @Query("DELETE FROM jugadores WHERE jugadorId = :jugadorId AND userId = :userId")
    suspend fun eliminarJugadorPorId(jugadorId: Long, userId: String)
}