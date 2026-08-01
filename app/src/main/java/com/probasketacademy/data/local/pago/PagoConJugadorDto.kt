package com.probasketacademy.data.local.pago

data class PagoConJugadorDto(
    val pagoId: Long,
    val concepto: String,
    val monto: Double,
    val fecha: String,
    val estado: String,
    val jugadorNombre: String,
    val numeroCamiseta: Int
)
