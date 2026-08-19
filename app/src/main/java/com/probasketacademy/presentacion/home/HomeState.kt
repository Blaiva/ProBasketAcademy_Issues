package com.probasketacademy.presentacion.home

import com.probasketacademy.domain.model.Pago

data class HomeState(
    val isLoading: Boolean = false,
    val jugadoresActivos: Int = 0,
    val asistenciaPromedio: String = "0%",
    val ingresosMes: String = "€0",
    val cobrosPendientes: List<Pago> = emptyList(),
    val errorMessage: String? = null
)