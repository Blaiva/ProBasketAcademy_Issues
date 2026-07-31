package com.probasketacademy.presentacion

import android.content.Context
import com.google.firebase.auth.FirebaseUser

data class AuthState(
    val isLoading: Boolean = false,
    val user: FirebaseUser? = null,
    val errorMessage: String? = null
)


sealed class AuthIntent {
    data class SignInWithGoogle(val context: Context) : AuthIntent()
    object SignOut : AuthIntent()
}