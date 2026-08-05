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
        SELECT 
            c.id AS id,
            c.nombre AS nombre,
            COUNT(j.jugadorId) AS totalJugadores
        FROM categorias c
        LEFT JOIN jugadores j ON c.id = j.categoriaId
        GROUP BY c.id
    """)
    fun obtenerCategoriasConConteo(): Flow<List<CategoriaConConteoDto>>
}