package com.probasketacademy.data.local.evento

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "eventos")
data class EventoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val titulo: String,
    val tipo: String,
    val fechaHoraEpocaMs: Long,
    val duracionHoras: Float,
    val lugar: String,
    val categoriaId: Long? = null
)
