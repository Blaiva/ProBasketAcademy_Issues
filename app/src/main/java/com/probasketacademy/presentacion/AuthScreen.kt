package com.probasketacademy.presentacion

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsBasketball
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage

@Composable
fun AuthScreen(viewModel: AuthViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Box(contentAlignment = Alignment.Center) {
            when {
                state.isLoading -> CircularProgressIndicator()

                state.user != null -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (state.user?.photoUrl != null) {
                            AsyncImage(
                                model = state.user?.photoUrl,
                                contentDescription = "Foto de perfil",
                                modifier = Modifier
                                    .size(100.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.SportsBasketball,
                                contentDescription = "Profile",
                                modifier = Modifier.size(100.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("¡Bienvenido a Pro Basket Academy!", style = MaterialTheme.typography.titleMedium)
                        Text(state.user?.displayName ?: "Usuario", style = MaterialTheme.typography.headlineMedium)
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(onClick = { viewModel.processIntent(AuthIntent.SignOut) }) {
                            Text("Cerrar Sesión")
                        }
                    }
                }

                else -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SportsBasketball,
                            contentDescription = "Logo",
                            modifier = Modifier.size(120.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Pro Basket Academy", style = MaterialTheme.typography.headlineLarge)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Tu academia de baloncesto", style = MaterialTheme.typography.bodyMedium)
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(onClick = { viewModel.processIntent(AuthIntent.SignInWithGoogle(context)) }) {
                            Text("Entrar con Google")
                        }
                        if (state.errorMessage != null) {
                            Text(state.errorMessage!!, color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }
}
