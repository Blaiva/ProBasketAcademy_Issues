package com.probasketacademy.presentacion.finanzas.list

sealed interface PagosListEvent {
    data class OnSearchQueryChanged(val query: String) : PagosListEvent
}