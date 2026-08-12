package com.probasketacademy.data.mapper

import com.probasketacademy.data.local.asistencia.AsistenciaEntity
import com.probasketacademy.data.local.asistencia.AsistenciaJugadorDto
import com.probasketacademy.domain.model.Asistencia

fun AsistenciaJugadorDto.toDomain(categoriaId: Long, fechaTimestamp: Long): Asistencia = Asistencia(
    jugadorId = jugadorId,
    categoriaId = categoriaId,
    fechaEpocaMs = fechaTimestamp,
    asistio = asistio ?: false,
    nombreJugador = nombreJugador,
    fotoUri = fotoUri
)

fun AsistenciaEntity.toDomain(): Asistencia = Asistencia(
    id = id,
    jugadorId = jugadorId,
    categoriaId = categoriaId,
    fechaEpocaMs = fechaEpocaMs,
    asistio = asistio
)

fun Asistencia.toEntity(): AsistenciaEntity = AsistenciaEntity(
    id = id,
    jugadorId = jugadorId,
    categoriaId = categoriaId,
    fechaEpocaMs = fechaEpocaMs,
    asistio = asistio
)