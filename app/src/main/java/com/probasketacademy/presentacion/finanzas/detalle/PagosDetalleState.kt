package com.probasketacademy.presentacion.finanzas.detalle

import com.probasketacademy.domain.model.Jugador
import com.probasketacademy.domain.model.Pago

data class PagosDetalleState(
    val isLoading: Boolean = false,
    val jugador: Jugador? = null,
    val saldoPendiente: Double = 0.0,
    val pagos: List<Pago> = emptyList(),
    val showPagoDialog: Boolean = false,
    val errorMessage: String? = null
)