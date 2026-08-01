package com.probasketacademy.data.mapper

import com.probasketacademy.data.local.jugador.JugadorConCategoriaDto
import com.probasketacademy.data.local.jugador.JugadorEntity
import com.probasketacademy.domain.model.Jugador

fun JugadorConCategoriaDto.toDomain(): Jugador = Jugador(
    id = id,
    categoriaId = 0L,
    nombre = nombre,
    numeroCamiseta = 0,
    posicion = posicion,
    estaActivo = estaActivo,
    docCompleta = docCompleta,
    estaturaM = 0f,
    pesoKg = 0f,
    fechaNacimiento = "",
    fotoUri = fotoUri,
    categoriaNombre = categoriaNombre
)

fun JugadorEntity.toDomain(): Jugador = Jugador(
    id = id,
    categoriaId = categoriaId,
    nombre = nombre,
    numeroCamiseta = numeroCamiseta,
    posicion = posicion,
    estaActivo = estaActivo,
    docCompleta = docCompleta,
    estaturaM = estaturaM,
    pesoKg = pesoKg,
    fechaNacimiento = fechaNacimiento,
    fotoUri = fotoUri
)

fun Jugador.toEntity(): JugadorEntity = JugadorEntity(
    id = id,
    categoriaId = categoriaId,
    nombre = nombre,
    numeroCamiseta = numeroCamiseta,
    posicion = posicion,
    estaActivo = estaActivo,
    docCompleta = docCompleta,
    estaturaM = estaturaM,
    pesoKg = pesoKg,
    fechaNacimiento = fechaNacimiento,
    fotoUri = fotoUri
)