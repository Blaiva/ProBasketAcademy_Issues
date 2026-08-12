package com.probasketacademy.presentacion.finanzas.detalle

import com.probasketacademy.domain.model.Pago

sealed interface PagosDetalleEvent {
    data object OnTogglePagoDialog : PagosDetalleEvent
    data class OnRegistrarPago(val montoS: String, val esMensual: Boolean) : PagosDetalleEvent
    data class OnMarcarComoPagado(val pago: Pago) : PagosDetalleEvent // <-- NUEVO EVENTO
}