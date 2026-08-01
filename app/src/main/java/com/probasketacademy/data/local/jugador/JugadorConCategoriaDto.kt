package com.probasketacademy.data.local.jugador

data class JugadorConCategoriaDto(
    val id: Long,
    val nombre: String,
    val posicion: String,
    val estaActivo: Boolean,
    val docCompleta: Boolean,
    val fotoUri: String?,
    val categoriaNombre: String
)
