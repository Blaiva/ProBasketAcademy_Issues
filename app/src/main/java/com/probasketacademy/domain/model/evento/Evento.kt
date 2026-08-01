package com.probasketacademy.domain.model.evento

data class Evento(
    val id: Long = 0,
    val titulo: String = "",
    val tipo: String = "",
    val fechaHoraEpocaMs: Long = 0,
    val duracionHoras: Float = 0f,
    val lugar: String = "",
    val categoriaId: Long? = null
)
