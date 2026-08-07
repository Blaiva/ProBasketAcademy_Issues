package com.probasketacademy.presentacion.iniciodesesion

data class AuthState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false
)