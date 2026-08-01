package com.probasketacademy.data.mapper

import com.probasketacademy.data.local.categoria.CategoriaConConteoDto
import com.probasketacademy.data.local.categoria.CategoriaEntity
import com.probasketacademy.domain.model.Categoria

fun CategoriaConConteoDto.toDomain(): Categoria = Categoria(
    id = id,
    nombre = nombre,
    totalJugadores = totalJugadores
)

fun Categoria.toEntity(): CategoriaEntity = CategoriaEntity(
    id = id,
    nombre = nombre
)