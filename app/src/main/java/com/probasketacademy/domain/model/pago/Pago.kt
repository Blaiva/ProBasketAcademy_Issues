package com.probasketacademy.domain.model.pago

data class Pago (
    val id: Long = 0,
    val jugadorId: Long = 0,
    val concepto: String = "",
    val monto: Double = 0.0,
    val fecha: String = "",
    val estado: String = "",
    val jugadorNombre: String = "",
    val numeroCamiseta: Int = 0
)