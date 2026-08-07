package com.probasketacademy.presentacion.iniciodesesion

import android.content.Context

sealed interface AuthEvent {
    data class OnGoogleSignInClicked(val context: Context) : AuthEvent
}