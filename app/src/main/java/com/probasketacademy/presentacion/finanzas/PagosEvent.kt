package com.probasketacademy.presentacion.finanzas

sealed interface PagosEvent {
    data object OnEnviarRecordatorio : PagosEvent
    data object OnRegistrarPago : PagosEvent
}