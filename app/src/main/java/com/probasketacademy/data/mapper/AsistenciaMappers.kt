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

fun Asistencia.toEntity(): AsistenciaEntity = AsistenciaEntity(
    id = id,
    jugadorId = jugadorId,
    categoriaId = if (categoriaId == 0L) null else categoriaId,
    fechaEpocaMs = fechaEpocaMs,
    asistio = asistio
)