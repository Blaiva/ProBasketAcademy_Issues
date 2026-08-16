package com.probasketacademy.data.mapper

import com.probasketacademy.data.local.jugador.JugadorConCategoriaDto
import com.probasketacademy.data.local.jugador.JugadorEntity
import com.probasketacademy.domain.model.Jugador

fun JugadorEntity.toDomain(): Jugador = Jugador(
    jugadorId = jugadorId,
    nombre = nombre,
    telefono = telefono,
    edad = edad,
    domicilio = domicilio,
    categoriaId = categoriaId,
    tallaCamiseta = tallaCamiseta,
    numeroCamiseta = numeroCamiseta,
    estatura = estatura,
    peso = peso,
    tutorNombre = tutorNombre,
    tutorTelefono = tutorTelefono,
    tutorVinculo = tutorVinculo,
    tutorCorreo = tutorCorreo,
    estado = estado,
    docCompleta = docCompleta,
    fotoUri = fotoUri,
    tipoInscripcion = tipoInscripcion,
    fechaInicio = fechaInicio,
    fechaVencimiento = fechaVencimiento,
    cuota = cuota,
    totalGenerado = totalGenerado,
    totalPagado = totalPagado,
    deudaActual = deudaActual
)

fun Jugador.toEntity(): JugadorEntity = JugadorEntity(
    jugadorId = jugadorId,
    nombre = nombre,
    telefono = telefono,
    edad = edad,
    domicilio = domicilio,
    categoriaId = if (this.categoriaId == 0L) null else this.categoriaId,
    tallaCamiseta = tallaCamiseta,
    numeroCamiseta = numeroCamiseta,
    estatura = estatura,
    peso = peso,
    tutorNombre = tutorNombre,
    tutorTelefono = tutorTelefono,
    tutorVinculo = tutorVinculo,
    tutorCorreo = tutorCorreo,
    estado = estado,
    docCompleta = docCompleta,
    fotoUri = fotoUri,
    tipoInscripcion = tipoInscripcion,
    fechaInicio = fechaInicio,
    fechaVencimiento = fechaVencimiento,
    cuota = cuota,
    totalGenerado = totalGenerado,
    totalPagado = totalPagado,
    deudaActual = deudaActual
)

fun JugadorConCategoriaDto.toDomain(): Jugador = Jugador(
    jugadorId = jugadorId,
    nombre = nombre,
    telefono = telefono,
    edad = edad,
    domicilio = domicilio,
    categoriaId = categoriaId,
    tallaCamiseta = tallaCamiseta,
    numeroCamiseta = numeroCamiseta,
    estatura = estatura,
    peso = peso,
    tutorNombre = tutorNombre,
    tutorTelefono = tutorTelefono,
    tutorVinculo = tutorVinculo,
    tutorCorreo = tutorCorreo,
    estado = estado,
    docCompleta = docCompleta,
    fotoUri = fotoUri,
    categoriaNombre = categoriaNombre ?: "Sin Categoría",
    tipoInscripcion = tipoInscripcion,
    fechaInicio = fechaInicio,
    fechaVencimiento = fechaVencimiento,
    cuota = cuota,
    totalGenerado = totalGenerado,
    totalPagado = totalPagado,
    deudaActual = deudaActual
)