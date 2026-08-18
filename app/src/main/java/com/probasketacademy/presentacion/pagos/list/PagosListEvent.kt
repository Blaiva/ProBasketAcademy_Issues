package com.probasketacademy.presentacion.pagos.list

sealed interface PagosListEvent {
    data class OnSearchQueryChanged(val query: String) : PagosListEvent
}