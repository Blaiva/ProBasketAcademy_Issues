package com.probasketacademy.presentacion.jugadores.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.probasketacademy.R
import com.probasketacademy.domain.model.Jugador
import com.probasketacademy.presentacion.perfil.ProfileDialog
import com.probasketacademy.ui.theme.* // Importamos todo de tu theme

@Composable
fun JugadoresListScreen(
    onNavigateToDetail: (Long) -> Unit,
    onAddJugador: () -> Unit,
    onLogout: () -> Unit,
    viewModel: JugadoresListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    var showProfileDialog by remember { mutableStateOf(false) }

    if (showProfileDialog) {
        ProfileDialog(
            user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser,
            onDismiss = { showProfileDialog = false },
            onLogout = { showProfileDialog = false; onLogout() }
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.onEvent(JugadoresListEvent.OnAddJugadorClicked)
                    onAddJugador()
                },
                containerColor = HeaderOrange,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Jugador")
            }
        },
        containerColor = LightBackground
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(bottom = padding.calculateBottomPadding())) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(HeaderOrange, RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
                    .padding(horizontal = 16.dp, vertical = 20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.White, CircleShape)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_probasket),
                                contentDescription = "Logo",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ProBasketAcademy", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable { showProfileDialog = true },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = "Perfil", tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Directorio de\nJugadores",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextDark,
                    lineHeight = 34.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Gestiona el roster, asistencia y rendimiento técnico.",
                    fontSize = 13.sp,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onEvent(JugadoresListEvent.OnSearchQueryChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar por nombre o posición...", color = TextMuted) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = BorderColor,
                        unfocusedContainerColor = CardBackground,
                        focusedContainerColor = CardBackground
                    ),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = HeaderOrange)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.jugadores) { jugador ->
                        JugadorItemRow(
                            jugador = jugador,
                            onClick = {
                                viewModel.onEvent(JugadoresListEvent.OnJugadorClicked(jugador.jugadorId))
                                onNavigateToDetail(jugador.jugadorId)
                            }
                        )
                    }
                    if (state.jugadores.isNotEmpty()) {
                        item {
                            Text(
                                text = "No hay más jugadores en esta categoría",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                color = TextMuted,
                                fontSize = 12.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun JugadorItemRow(jugador: Jugador, onClick: () -> Unit) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(BorderColor),
                contentAlignment = Alignment.Center
            ) {
                if (!jugador.fotoUri.isNullOrEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(jugador.fotoUri)
                            .crossfade(true)
                            .build(),
                        contentDescription = "Foto de ${jugador.nombre}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop // Recorta la imagen para que llene el círculo
                    )
                } else {
                    Text(
                        text = if (jugador.nombre.isNotEmpty()) jugador.nombre.take(1).uppercase() else "?",
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        fontSize = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = jugador.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                Text(
                    text = "${jugador.categoriaNombre} • Talla: ${jugador.tallaCamiseta.ifEmpty { "N/A" }}",
                    fontSize = 12.sp,
                    color = TextMuted
                )
            }

            val isActive = jugador.estado.equals("Activo", ignoreCase = true)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isActive) ChipActiveBg else ChipInactiveBg)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = jugador.estado,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isActive) ChipActiveText else ChipInactiveText
                )
            }

            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = ChevronColor)
        }
    }
}