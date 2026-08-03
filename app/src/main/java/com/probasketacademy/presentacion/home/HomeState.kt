package com.probasketacademy.presentacion.home

import com.probasketacademy.domain.model.Pago

data class HomeState(
    val isLoading: Boolean = false,
    val jugadoresActivos: Int = 145,
    val asistenciaPromedio: String = "91%",
    val ingresosMes: String = "€6,200",
    val cobrosPendientes: List<Pago> = emptyList(),
    val errorMessage: String? = null
)