package com.probasketacademy.presentacion.pagos.detalle

import com.probasketacademy.domain.model.Jugador
import com.probasketacademy.domain.model.Pago

data class PagosDetalleState(
    val isLoading: Boolean = false,
    val jugador: Jugador? = null,
    val saldoPendiente: Double = 0.0,
    val pagos: List<Pago> = emptyList(),
    val showPagoDialog: Boolean = false,
    val showAbonoDialog: Boolean = false,
    val selectedPagoParaAbono: Pago? = null,

    val conceptoInput: String = "",
    val montoTotalInput: String = "",
    val montoAbonadoInput: String = "",

    val tipoInscripcion: String = "Mensual",
    val fechaInicio: String = "",
    val fechaVencimiento: String = "",

    val montoNuevoAbonoInput: String = "",
    val montoNuevoAbonoError: String? = null,

    val showSaldarConfirmDialog: Boolean = false,
    val pagoParaSaldar: Pago? = null,

    val errorMessage: String? = null
)