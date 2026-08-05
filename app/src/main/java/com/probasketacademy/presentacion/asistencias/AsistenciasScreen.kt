package com.probasketacademy.presentacion.asistencias

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.probasketacademy.R
import com.probasketacademy.domain.model.Jugador
import com.probasketacademy.ui.theme.*

@Composable
fun AsistenciasScreen(
    onNavigateBack: () -> Unit,
    viewModel: AsistenciasViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onNavigateBack()
        }
    }

    Scaffold(
        containerColor = LightBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // --- ENCABEZADO ESTÁNDAR ---
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
                            .background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- TÍTULO Y SUBTÍTULO ---
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Pase de Lista",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextDark
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Categoría: ${state.categoriaNombre}",
                    fontSize = 14.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- LISTA DE JUGADORES ---
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = HeaderOrange)
                }
            } else {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, BorderColor) // Borde exterior unificado como en el diseño
                ) {
                    LazyColumn {
                        itemsIndexed(state.jugadores) { index, jugador ->
                            val isChecked = state.asistencias[jugador.jugadorId] ?: false

                            JugadorAsistenciaRow(
                                jugador = jugador,
                                isChecked = isChecked,
                                onCheckedChange = { viewModel.onEvent(AsistenciasEvent.OnJugadorToggled(jugador.jugadorId, it)) }
                            )

                            // Divisor entre elementos, excepto en el último
                            if (index < state.jugadores.size - 1) {
                                HorizontalDivider(color = DividerColor)
                            }
                        }
                    }
                }
            }

            // --- BOTONES INFERIORES ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onNavigateBack,
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B6B6B)) // Gris para Cancelar
                ) {
                    Text("Cancelar", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                }

                Button(
                    onClick = { viewModel.onEvent(AsistenciasEvent.OnConfirmarAsistencia) },
                    modifier = Modifier.weight(1f).height(56.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HeaderOrange) // Naranja para Confirmar
                ) {
                    Text("Confirmar\nAsistencia", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color.White, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                }
            }
        }
    }
}

@Composable
private fun JugadorAsistenciaRow(
    jugador: Jugador,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar (Iniciales)
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(BorderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = jugador.nombre.take(1).uppercase(),
                fontWeight = FontWeight.Bold,
                color = TextDark,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Nombre del jugador
        Text(
            text = jugador.nombre,
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = TextDark,
            modifier = Modifier.weight(1f)
        )

        // Checkbox cuadrado
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = TextDark, // Color oscuro como en la imagen
                checkmarkColor = Color.White
            )
        )
    }
}