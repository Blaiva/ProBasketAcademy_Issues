package com.probasketacademy.domain.model

data class Jugador(
    val id: Long = 0,
    val categoriaId: Long = 0,
    val nombre: String = "",
    val numeroCamiseta: Int = 0,
    val posicion: String = "",
    val estaActivo: Boolean = true,
    val docCompleta: Boolean = false,
    val estaturaM: Float = 0f,
    val pesoKg: Float = 0f,
    val fechaNacimiento: String = "",
    val fotoUri: String? = null,
    val categoriaNombre: String = ""
)