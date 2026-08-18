package com.probasketacademy.data.mapper

import com.probasketacademy.data.local.evento.EventoEntity
import com.probasketacademy.domain.model.Evento

fun EventoEntity.toDomain(): Evento = Evento(
    id = id,
    titulo = titulo,
    tipo = tipo,
    fechaHoraEpocaMs = fechaHoraEpocaMs,
    duracionHoras = duracionHoras,
    lugar = lugar,
    categoriaId = categoriaId
)

fun Evento.toEntity(userId: String): EventoEntity = EventoEntity(
    id = id,
    titulo = titulo,
    tipo = tipo,
    fechaHoraEpocaMs = fechaHoraEpocaMs,
    duracionHoras = duracionHoras,
    lugar = lugar,
    categoriaId = categoriaId,
    userId = userId
)