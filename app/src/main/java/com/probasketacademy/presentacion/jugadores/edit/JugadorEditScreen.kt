package com.probasketacademy.presentacion.jugadores.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.probasketacademy.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JugadorEditScreen(
    jugadorId: Long,
    onNavigateBack: () -> Unit,
    viewModel: JugadorEditViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val jugador = state.jugador

    LaunchedEffect(jugadorId) {
        viewModel.cargarJugador(jugadorId)
    }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            onNavigateBack()
        }
    }

    // --- SISTEMA DE MODOS ---
    // Si el ID es 0, forzamos el modo edición para que salgan las barras al crear.
    var isEditing by remember(jugador) {
        mutableStateOf(jugador?.jugadorId == 0L)
    }

    // --- VARIABLES DE LAS BARRAS DE TEXTO ---
    var nombre by remember(jugador) { mutableStateOf(jugador?.nombre ?: "") }
    var numero by remember(jugador) { mutableStateOf(jugador?.numeroCamiseta?.toString() ?: "") }
    var talla by remember(jugador) { mutableStateOf(jugador?.tallaCamiseta ?: "") }
    var categoria by remember(jugador) { mutableStateOf(jugador?.categoriaNombre ?: "") }
    var estatura by remember(jugador) { mutableStateOf(jugador?.estatura?.toString() ?: "") }
    var peso by remember(jugador) { mutableStateOf(jugador?.peso?.toString() ?: "") }
    var edad by remember(jugador) { mutableStateOf(jugador?.edad?.toString() ?: "") }
    var telefono by remember(jugador) { mutableStateOf(jugador?.telefono ?: "") }
    var estado by remember(jugador) { mutableStateOf(jugador?.estado ?: "Activo") }
    var docCompleta by remember(jugador) { mutableStateOf(jugador?.docCompleta ?: false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) { if (jugador?.jugadorId == 0L) "Nuevo Jugador" else "Editar Jugador" } else "Ficha del Jugador",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                actions = {
                    // Botón para cambiar a modo edición si estamos en modo lectura
                    if (!isEditing && jugador?.jugadorId != 0L) {
                        IconButton(onClick = { isEditing = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White)
                        }
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

            if (isEditing) {
                // ==========================================
                // 1. MODO EDICIÓN / CREACIÓN (BARRAS DE TEXTO)
                // ==========================================
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text("Información Personal", fontWeight = FontWeight.Bold, color = PrimaryOrange)

                    OutlinedTextField(
                        value = nombre,
                        onValueChange = { nombre = it },
                        label = { Text("Nombre Completo") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = edad,
                            onValueChange = { edad = it },
                            label = { Text("Edad") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = telefono,
                            onValueChange = { telefono = it },
                            label = { Text("Teléfono") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Datos Deportivos", fontWeight = FontWeight.Bold, color = PrimaryOrange)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = categoria,
                            onValueChange = { categoria = it },
                            label = { Text("Categoría") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = numero,
                            onValueChange = { numero = it },
                            label = { Text("No. Camiseta") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = estatura,
                            onValueChange = { estatura = it },
                            label = { Text("Estatura (m)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                        OutlinedTextField(
                            value = peso,
                            onValueChange = { peso = it },
                            label = { Text("Peso (kg)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    OutlinedTextField(
                        value = talla,
                        onValueChange = { talla = it },
                        label = { Text("Talla de Camiseta") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Estado y Documentación", fontWeight = FontWeight.Bold, color = PrimaryOrange)

                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Jugador Activo", color = TextDark, fontWeight = FontWeight.Medium)
                                Switch(
                                    checked = estado.equals("Activo", ignoreCase = true),
                                    onCheckedChange = { isChecked -> estado = if (isChecked) "Activo" else "Inactivo" },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = SuccessGreen)
                                )
                            }
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BorderColor)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Documentación Completa", color = TextDark, fontWeight = FontWeight.Medium)
                                Switch(
                                    checked = docCompleta,
                                    onCheckedChange = { docCompleta = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = SuccessGreen)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            // NOTA: Aquí debes asegurarte de enviar estas variables locales a tu ViewModel.
                            // Si tu evento OnGuardarClicked necesita parámetros, pásaselos aquí.
                            viewModel.onEvent(JugadorEditEvent.OnGuardarClicked)
                        },
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
                            Text("Guardar Jugador", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                        }
                    }

                    if (jugador.jugadorId != 0L) {
                        OutlinedButton(
                            onClick = { isEditing = false },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancelar Edición", color = TextDark)
                        }
                    }
                }
            } else {
                // ==========================================
                // 2. MODO LECTURA (VISTA DE FOTO ORIGINAL)
                // ==========================================
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
                                .background(BorderColor),
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
                                text = "#${jugador.numeroCamiseta}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = jugador.nombre, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
                    Text(
                        text = "Talla: ${jugador.tallaCamiseta.ifEmpty { "No asignada" }}",
                        fontSize = 14.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val isActive = jugador.estado.equals("Activo", ignoreCase = true)
                        AssistChip(
                            onClick = { },
                            label = { Text(jugador.estado, color = if (isActive) ActiveBadgeText else TextMuted, fontWeight = FontWeight.Bold) },
                            leadingIcon = {
                                Box(modifier = Modifier.size(8.dp).background(if (isActive) ActiveBadgeText else TextMuted, CircleShape))
                            },
                            colors = AssistChipDefaults.assistChipColors(containerColor = if (isActive) ActiveBadgeBg else ChipInactiveBg),
                            border = null
                        )
                        AssistChip(
                            onClick = { },
                            label = { Text(if (jugador.docCompleta) "Doc. Completa" else "Doc. Pendiente", color = TextDark) },
                            leadingIcon = {
                                if (jugador.docCompleta) Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            InfoCard(modifier = Modifier.weight(1f), title = "CATEGORÍA", value = jugador.categoriaNombre, subtitle = "Asignada")
                            InfoCard(modifier = Modifier.weight(1f), title = "FÍSICO", value = "${jugador.estatura} m", subtitle = "${jugador.peso} kg")
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            InfoCard(modifier = Modifier.weight(1f), title = "EDAD", value = "${jugador.edad} años", subtitle = "Registrada")
                            InfoCard(modifier = Modifier.weight(1f), title = "CONTACTO", value = jugador.telefono.ifEmpty { "N/A" }, subtitle = "Principal")
                        }
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