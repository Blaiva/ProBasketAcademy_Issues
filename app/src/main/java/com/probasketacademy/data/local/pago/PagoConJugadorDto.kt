package com.probasketacademy.data.local.pago

data class PagoConJugadorDto(
    val pagoId: Long,
    val jugadorId: Long,
    val concepto: String,
    val montoTotal: Double,
    val montoPagado: Double,
    val deuda: Double,
    val fecha: String,
    val estado: String,
    val jugadorNombre: String,
    val numeroCamiseta: Int
)