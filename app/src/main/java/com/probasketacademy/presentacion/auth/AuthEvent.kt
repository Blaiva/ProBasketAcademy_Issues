package com.probasketacademy.presentacion.auth

import android.content.Context

sealed interface AuthEvent {
    data class OnGoogleSignInClicked(val context: Context) : AuthEvent
}