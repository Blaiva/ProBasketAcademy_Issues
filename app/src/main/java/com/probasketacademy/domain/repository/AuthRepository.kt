package com.probasketacademy.domain.repository

import android.content.Context
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun signInWithGoogle(context: Context): Result<FirebaseUser>
    suspend fun signOut()
    fun getCurrentUser(): FirebaseUser?
}