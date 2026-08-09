package com.probasketacademy.di

import android.content.Context
import androidx.credentials.CredentialManager
import com.probasketacademy.data.repository.AuthRepositoryImpl
import com.google.firebase.auth.FirebaseAuth
import com.probasketacademy.domain.repository.AuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideCredentialManager(@ApplicationContext context: Context): CredentialManager =
        CredentialManager.create(context)

    @Provides
    @Singleton
    fun provideAuthRepository(
        auth: FirebaseAuth,
        credentialManager: CredentialManager
    ): AuthRepository = AuthRepositoryImpl(auth, credentialManager)
}
