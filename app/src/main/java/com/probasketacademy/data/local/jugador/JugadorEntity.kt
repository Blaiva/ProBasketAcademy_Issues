package com.probasketacademy.data.local.jugador

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.probasketacademy.data.local.categoria.CategoriaEntity

@Entity(
    tableName = "jugadores",
    foreignKeys = [
        ForeignKey(
            entity = CategoriaEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoriaId"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [Index(value = ["categoriaId"])]
)
data class JugadorEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val categoriaId: Long,
    val nombre: String,
    val numeroCamiseta: Int,
    val posicion: String,
    val estaActivo: Boolean,
    val docCompleta: Boolean,
    val estaturaM: Float,
    val pesoKg: Float,
    val fechaNacimiento: String,
    val fotoUri: String? = null
)