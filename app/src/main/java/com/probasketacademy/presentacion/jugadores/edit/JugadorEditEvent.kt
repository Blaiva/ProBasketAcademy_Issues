package com.probasketacademy.presentacion.jugadores.edit

sealed interface JugadorEditEvent {
    data class OnNombreChanged(val value: String) : JugadorEditEvent
    data class OnTelefonoChanged(val value: String) : JugadorEditEvent
    data class OnEdadChanged(val value: String) : JugadorEditEvent
    data class OnDomicilioChanged(val value: String) : JugadorEditEvent
    data class OnCategoriaSelected(val id: Long, val nombre: String) : JugadorEditEvent
    data class OnTallaCamisetaChanged(val value: String) : JugadorEditEvent
    data class OnNumeroCamisetaChanged(val value: String) : JugadorEditEvent
    data class OnEstaturaChanged(val value: String) : JugadorEditEvent
    data class OnPesoChanged(val value: String) : JugadorEditEvent
    data class OnTutorNombreChanged(val value: String) : JugadorEditEvent
    data class OnTutorTelefonoChanged(val value: String) : JugadorEditEvent
    data class OnTutorVinculoChanged(val value: String) : JugadorEditEvent
    data class OnTutorCorreoChanged(val value: String) : JugadorEditEvent
    data class OnEstadoChanged(val value: String) : JugadorEditEvent
    data class OnDocCompletaChanged(val value: Boolean) : JugadorEditEvent
    data object OnGuardarClicked : JugadorEditEvent
    data object OnEliminarClicked : JugadorEditEvent
}