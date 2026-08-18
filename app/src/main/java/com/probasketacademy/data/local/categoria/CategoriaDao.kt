package com.probasketacademy.data.local.categoria

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriaDao {
    @Upsert
    suspend fun guardarCategoria(categoria: CategoriaEntity)

    @Query("""
        SELECT c.id AS id, c.nombre AS nombre, COUNT(j.jugadorId) AS totalJugadores
        FROM categorias c
        LEFT JOIN jugadores j ON c.id = j.categoriaId AND j.userId = :userId
        WHERE c.userId = :userId
        GROUP BY c.id
    """)
    fun obtenerCategoriasConConteo(userId: String): Flow<List<CategoriaConConteoDto>>

    @Query("""
        SELECT c.id, c.nombre, COUNT(j.jugadorId) AS totalJugadores 
        FROM categorias c 
        LEFT JOIN jugadores j ON c.id = j.categoriaId AND j.userId = :userId
        WHERE c.id = :id AND c.userId = :userId
        GROUP BY c.id, c.nombre
    """)
    fun obtenerCategoriaConConteoPorId(id: Long, userId: String): Flow<CategoriaConConteoDto?>

    @Query("DELETE FROM categorias WHERE id = :id AND userId = :userId")
    suspend fun eliminarCategoria(id: Long, userId: String)
}