package com.probasketacademy.presentacion.jugadores.edit

import com.probasketacademy.domain.model.Jugador

data class JugadorEditState(
    val isLoading: Boolean = false,
    val jugador: Jugador? = null,
    val errorMessage: String? = null,
    val isSaved: Boolean = false
)