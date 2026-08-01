package com.probasketacademy.data.local.asistencia

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.probasketacademy.data.local.categoria.CategoriaEntity
import com.probasketacademy.data.local.jugador.JugadorEntity

@Entity(
    tableName = "asistencias",
    foreignKeys = [
        ForeignKey(
            entity = JugadorEntity::class,
            parentColumns = ["id"],
            childColumns = ["jugadorId"],
            onDelete = ForeignKey.Companion.CASCADE
        ),
        ForeignKey(
            entity = CategoriaEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoriaId"],
            onDelete = ForeignKey.Companion.CASCADE
        )
    ],
    indices = [Index(value = ["jugadorId"]), Index(value = ["categoriaId"])]
)
data class AsistenciaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val jugadorId: Long,
    val categoriaId: Long,
    val fechaEpocaMs: Long,
    val asistio: Boolean
)