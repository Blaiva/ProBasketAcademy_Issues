package com.probasketacademy.presentacion.pagos.detalle

import com.probasketacademy.domain.model.Pago

sealed interface PagosDetalleEvent {
    data object OnTogglePagoDialog : PagosDetalleEvent
    data class OnToggleAbonoDialog(val pago: Pago? = null) : PagosDetalleEvent
    data class OnConceptoChanged(val concepto: String) : PagosDetalleEvent
    data class OnMontoTotalChanged(val monto: String) : PagosDetalleEvent
    data class OnMontoAbonadoChanged(val monto: String) : PagosDetalleEvent
    data class OnMontoNuevoAbonoChanged(val monto: String) : PagosDetalleEvent
    data class OnTipoInscripcionChanged(val value: String) : PagosDetalleEvent
    data class OnFechaInicioChanged(val value: String) : PagosDetalleEvent
    data object OnRegistrarPago : PagosDetalleEvent
    data object OnRegistrarAbono : PagosDetalleEvent
    data class OnToggleSaldarConfirmDialog(val pago: Pago? = null) : PagosDetalleEvent
    data object OnConfirmarSaldar : PagosDetalleEvent
}