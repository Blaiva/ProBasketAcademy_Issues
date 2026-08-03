package com.probasketacademy.presentacion.jugadores.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

private val HeaderOrange = Color(0xFFB34A1B)
private val LightBackground = Color(0xFFF4F6F8)
private val CardBackground = Color.White
private val TextDark = Color(0xFF1E293B)
private val TextMuted = Color(0xFF94A3B8)
private val PrimaryOrange = Color(0xFFE5634D)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JugadorEditScreen(
    onNavigateBack: () -> Unit,
    viewModel: JugadorEditViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val jugador = state.jugador

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Ficha del Jugador", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = HeaderOrange)
            )
        },
        containerColor = LightBackground
    ) { padding ->
        if (state.isLoading && jugador == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = HeaderOrange)
            }
        } else if (jugador != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.padding(top = 16.dp)) {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE2E8F0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = jugador.nombre.take(1).uppercase(),
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextMuted
                        )
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .offset(x = 8.dp, y = 8.dp)
                            .size(36.dp)
                            .background(HeaderOrange, CircleShape)
                            .border(2.dp, LightBackground, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "#${jugador.numeroCamiseta}", // Campo real de la DB[cite: 4]
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(text = jugador.nombre, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
                Text(
                    text = "Talla: ${jugador.tallaCamiseta.ifEmpty { "No asignada" }}", // Campo real de la DB[cite: 4]
                    fontSize = 14.sp,
                    color = TextMuted
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val isActive = jugador.estado.equals("Activo", ignoreCase = true)
                    AssistChip(
                        onClick = { },
                        label = { Text(jugador.estado, color = if (isActive) Color(0xFF2563EB) else TextMuted, fontWeight = FontWeight.Bold) }, // Campo real de la DB[cite: 4]
                        leadingIcon = {
                            Box(modifier = Modifier.size(8.dp).background(if (isActive) Color(0xFF2563EB) else TextMuted, CircleShape))
                        },
                        colors = AssistChipDefaults.assistChipColors(containerColor = if (isActive) Color(0xFFDBEAFE) else Color(0xFFF1F5F9)),
                        border = null
                    )

                    AssistChip(
                        onClick = { },
                        label = { Text(if (jugador.docCompleta) "Doc. Completa" else "Doc. Pendiente", color = TextDark) }, // Campo real de la DB[cite: 4]
                        leadingIcon = {
                            if (jugador.docCompleta) Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(16.dp))
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        InfoCard(
                            modifier = Modifier.weight(1f),
                            title = "CATEGORÍA",
                            value = jugador.categoriaNombre, // Campo real de la DB[cite: 4]
                            subtitle = "Asignada"
                        )
                        InfoCard(
                            modifier = Modifier.weight(1f),
                            title = "FÍSICO",
                            value = "${jugador.estatura} m", // Campo real de la DB[cite: 4]
                            subtitle = "${jugador.peso} kg" // Campo real de la DB[cite: 4]
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        InfoCard(
                            modifier = Modifier.weight(1f),
                            title = "EDAD",
                            value = "${jugador.edad} años", // Campo real de la DB[cite: 4]
                            subtitle = "Registrada"
                        )
                        InfoCard(
                            modifier = Modifier.weight(1f),
                            title = "CONTACTO",
                            value = jugador.telefono.ifEmpty { "N/A" }, // Campo real de la DB[cite: 4]
                            subtitle = "Principal"
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { viewModel.onEvent(JugadorEditEvent.OnGuardarClicked) },
                    enabled = !state.isLoading,
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HeaderOrange)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Guardar Cambios", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoCard(modifier: Modifier = Modifier, title: String, value: String, subtitle: String) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
            Text(text = subtitle, fontSize = 12.sp, color = TextMuted)
        }
    }
}