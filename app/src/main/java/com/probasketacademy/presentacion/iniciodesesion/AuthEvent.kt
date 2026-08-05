package com.probasketacademy.presentacion.iniciodesesion

import android.content.Context

sealed interface AuthEvent {
    data class OnEmailChanged(val email: String) : AuthEvent
    data class OnPasswordChanged(val password: String) : AuthEvent
    data object OnTogglePasswordVisibility : AuthEvent
    data object OnLoginClicked : AuthEvent
    data class OnGoogleSignInClicked(val context: Context) : AuthEvent
    data object OnForgotPasswordClicked : AuthEvent
}