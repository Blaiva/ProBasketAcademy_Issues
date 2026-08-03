package com.probasketacademy.presentacion.jugadores.edit

sealed interface JugadorEditEvent {
    data object OnGuardarClicked : JugadorEditEvent
}