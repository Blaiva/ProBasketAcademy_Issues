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
    fotoUri = fotoUri
)

fun Jugador.toEntity(): JugadorEntity = JugadorEntity(
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
    fotoUri = fotoUri
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
    categoriaNombre = categoriaNombre
)