package com.probasketacademy.presentacion.finanzas

import com.probasketacademy.domain.model.Jugador
import com.probasketacademy.domain.model.Pago

data class PagosState(
    val isLoading: Boolean = false,
    val jugador: Jugador? = null,
    val saldoPendiente: Double = 0.0,
    val pagos: List<Pago> = emptyList(),
    val errorMessage: String? = null
)