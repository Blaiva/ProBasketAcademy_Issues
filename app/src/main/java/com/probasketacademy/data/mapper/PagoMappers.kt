package com.probasketacademy.data.mapper

import com.probasketacademy.data.local.pago.PagoConJugadorDto
import com.probasketacademy.data.local.pago.PagoEntity
import com.probasketacademy.domain.model.Pago

fun PagoConJugadorDto.toDomain(): Pago = Pago(
    id = pagoId,
    jugadorId = jugadorId,
    concepto = concepto,
    montoTotal = montoTotal,
    montoPagado = montoPagado,
    deuda = deuda,
    fecha = fecha,
    estado = estado,
    jugadorNombre = jugadorNombre,
    numeroCamiseta = numeroCamiseta
)

fun Pago.toEntity(): PagoEntity = PagoEntity(
    id = id,
    jugadorId = jugadorId,
    concepto = concepto,
    montoTotal = montoTotal,
    montoPagado = montoPagado,
    deuda = deuda,
    fecha = fecha,
    estado = estado
)