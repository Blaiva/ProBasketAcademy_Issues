package com.probasketacademy.presentacion.finanzas.detalle

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.probasketacademy.domain.model.Pago
import com.probasketacademy.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagosDetalleScreen(
    jugadorId: Long,
    onNavigateBack: () -> Unit,
    viewModel: PagosDetalleViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(jugadorId) {
        viewModel.cargarDatos(jugadorId)
    }

    if (state.showPagoDialog) {
        var monto by remember { mutableStateOf("") }
        var esMensual by remember { mutableStateOf(true) }

        AlertDialog(
            onDismissRequest = { viewModel.onEvent(PagosDetalleEvent.OnTogglePagoDialog) },
            title = { Text("Registrar Pago", fontWeight = FontWeight.Bold, color = TextDark) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Selecciona el tipo de suscripción para el jugador:", fontSize = 13.sp, color = TextMuted)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = !esMensual,
                                onClick = { esMensual = false },
                                colors = RadioButtonDefaults.colors(selectedColor = HeaderOrange)
                            )
                            Text("Semanal", fontSize = 14.sp, color = TextDark)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = esMensual,
                                onClick = { esMensual = true },
                                colors = RadioButtonDefaults.colors(selectedColor = HeaderOrange)
                            )
                            Text("Mensual", fontSize = 14.sp, color = TextDark)
                        }
                    }

                    OutlinedTextField(
                        value = monto,
                        onValueChange = { monto = it },
                        label = { Text("Monto a pagar") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onEvent(PagosDetalleEvent.OnRegistrarPago(monto, esMensual)) },
                    colors = ButtonDefaults.buttonColors(containerColor = HeaderOrange)
                ) { Text("Guardar Pago", color = Color.White, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onEvent(PagosDetalleEvent.OnTogglePagoDialog) }) { Text("Cancelar", color = TextMuted) }
            },
            containerColor = CardBackground
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Estado de Cuenta", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp) },
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
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = HeaderOrange)
                }
            } else {
                Spacer(modifier = Modifier.height(16.dp))

                state.jugador?.let { jugador ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier.size(56.dp).background(LightBackground, CircleShape),
                                contentAlignment = Alignment.Center
                            ) { Text(jugador.nombre.take(1).uppercase(), fontWeight = FontWeight.Bold, fontSize = 24.sp, color = TextDark) }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(jugador.nombre.uppercase(), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TextDark)
                                Text("#${jugador.numeroCamiseta}, ${jugador.categoriaNombre.ifEmpty { "Sin Categoría" }}", fontSize = 13.sp, color = TextMuted)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Saldo Pendiente:", fontSize = 13.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("$${state.saldoPendiente.toInt()}", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)

                        Spacer(modifier = Modifier.height(12.dp))

                        val isAlDia = state.saldoPendiente <= 0
                        val statusBg = if (isAlDia) Color(0xFFE8F5E9) else BadgeUrgentBg
                        val statusText = if (isAlDia) "Al día" else "Con deudas"
                        val statusColor = if (isAlDia) SuccessGreen else BadgeUrgentText

                        Row(
                            modifier = Modifier.clip(RoundedCornerShape(8.dp)).background(statusBg).padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = statusColor, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(statusText, color = statusColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth().weight(1f).padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth().background(LightBackground).padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Fecha", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, modifier = Modifier.weight(0.8f))
                            Text("Concepto", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, modifier = Modifier.weight(1.5f))
                            Text("Estado", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                        }
                        HorizontalDivider(color = DividerColor)

                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(state.pagos) { pago ->
                                // Pasamos el evento onMarcarPagado
                                PagoItemRow(
                                    pago = pago,
                                    onMarcarPagado = { viewModel.onEvent(PagosDetalleEvent.OnMarcarComoPagado(pago)) }
                                )
                                HorizontalDivider(color = DividerColor)
                            }
                            if (state.pagos.isEmpty()) {
                                item {
                                    Text("No hay pagos registrados para este jugador.", color = TextMuted, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(32.dp))
                                }
                            }
                        }
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth().background(CardBackground).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { viewModel.onEvent(PagosDetalleEvent.OnTogglePagoDialog) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HeaderOrange)
                    ) { Text("Registrar Nuevo Pago", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White) }
                }
            }
        }
    }
}

@Composable
private fun PagoItemRow(pago: Pago, onMarcarPagado: () -> Unit) {
    val isPending = pago.estado == "PENDIENTE"

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(pago.fecha, fontSize = 12.sp, color = TextDark, modifier = Modifier.weight(0.8f))

        Column(modifier = Modifier.weight(1.5f)) {
            Text(pago.concepto, fontSize = 13.sp, color = TextDark, fontWeight = FontWeight.Bold, maxLines = 2)
            Spacer(modifier = Modifier.height(4.dp))
            Text("$${pago.monto.toInt()}", fontSize = 12.sp, color = TextMuted)
        }

        Row(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.End) {
            if (isPending) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(BadgeUrgentBg)
                        .clickable { onMarcarPagado() }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Pagar",
                        color = BadgeUrgentText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE8F5E9))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Pagado",
                        color = SuccessGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}