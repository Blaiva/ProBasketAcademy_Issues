package com.probasketacademy.presentacion.iniciodesesion

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.probasketacademy.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        checkSesion()
    }

    private fun checkSesion(){
        authRepository.getCurrentUser()?.let { user ->
            _state.update { it.copy(user = user) }
        }
    }

    fun proccessIntent(intent: AuthEvent){
        when(intent){
            is AuthEvent.OnGoogleSignInClicked -> signIn(intent.context)
        }
    }

    private fun signIn(context: Context){
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            val result = authRepository.signInWithGoogle(context)

            result.fold(
                onSuccess = {user -> _state.update { it.copy(isLoading = false, user = user) }},
                onFailure = {e -> _state.update { it.copy(isLoading = false, errorMessage = e.message) }}
            )
        }
    }
}