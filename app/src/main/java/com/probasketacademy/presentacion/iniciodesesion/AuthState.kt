package com.probasketacademy.presentacion.iniciodesesion

import android.content.Context
import com.google.firebase.auth.FirebaseUser

data class AuthState(
    val email: String = "admin@basketacademy.com",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isAuthenticated: Boolean = false
)


sealed class AuthIntent {
    data class SignInWithGoogle(val context: Context) : AuthIntent()
    object SignOut : AuthIntent()
}