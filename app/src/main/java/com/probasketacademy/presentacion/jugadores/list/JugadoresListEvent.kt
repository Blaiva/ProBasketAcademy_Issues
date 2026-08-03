package com.probasketacademy.presentacion.jugadores.list


sealed interface JugadoresListEvent {
    data class OnSearchQueryChanged(val query: String) : JugadoresListEvent
    data class OnJugadorClicked(val id: Long) : JugadoresListEvent
    data object OnAddJugadorClicked : JugadoresListEvent
}