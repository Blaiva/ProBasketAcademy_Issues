package com.probasketacademy.presentacion.pagos.detalle

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.probasketacademy.domain.model.Jugador
import com.probasketacademy.domain.model.Pago
import com.probasketacademy.presentacion.finanzas.detalle.PagosDetalleViewModel
import com.probasketacademy.ui.theme.*

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

    PagosDetalleContent(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PagosDetalleContent(
    state: PagosDetalleState,
    onEvent: (PagosDetalleEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    if (state.showPagoDialog) {
        AlertDialog(
            onDismissRequest = { onEvent(PagosDetalleEvent.OnTogglePagoDialog) },
            title = { Text("Registrar Abono o Pago", fontWeight = FontWeight.Bold, color = TextDark) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Ingresa los detalles del cobro y el abono recibido:", fontSize = 13.sp, color = TextMuted)
                    OutlinedTextField(
                        value = state.conceptoInput,
                        onValueChange = { onEvent(PagosDetalleEvent.OnConceptoChanged(it)) },
                        label = { Text("Concepto (ej. Cuota Mensual)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = state.montoTotalInput,
                        onValueChange = { onEvent(PagosDetalleEvent.OnMontoTotalChanged(it)) },
                        label = { Text("Monto Total a Pagar") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    OutlinedTextField(
                        value = state.montoAbonadoInput,
                        onValueChange = { onEvent(PagosDetalleEvent.OnMontoAbonadoChanged(it)) },
                        label = { Text("Monto Abonado Hoy") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    HorizontalDivider(color = DividerColor)
                    Text("Detalles de Inscripción", fontWeight = FontWeight.Bold, color = PrimaryOrange, fontSize = 12.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        var inscripcionExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = inscripcionExpanded,
                            onExpandedChange = { inscripcionExpanded = !inscripcionExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = state.tipoInscripcion,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Tipo") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = inscripcionExpanded) },
                                modifier = Modifier.menuAnchor(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = inscripcionExpanded,
                                onDismissRequest = { inscripcionExpanded = false }
                            ) {
                                listOf("Mensual", "Semanal").forEach { tipo ->
                                    DropdownMenuItem(
                                        text = { Text(tipo) },
                                        onClick = {
                                            onEvent(PagosDetalleEvent.OnTipoInscripcionChanged(tipo))
                                            inscripcionExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                        OutlinedTextField(
                            value = state.fechaInicio,
                            onValueChange = { onEvent(PagosDetalleEvent.OnFechaInicioChanged(it)) },
                            label = { Text("Fecha Inicio") },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("dd/mm/yyyy") },
                            shape = RoundedCornerShape(12.dp)
                        )
                    }

                    Text("Vencimiento automático: ${state.fechaVencimiento}", fontSize = 11.sp, color = TextMuted)
                }
            },
            confirmButton = {
                Button(
                    onClick = { onEvent(PagosDetalleEvent.OnRegistrarPago) },
                    colors = ButtonDefaults.buttonColors(containerColor = HeaderOrange)
                ) { Text("Guardar Pago", color = Color.White, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { onEvent(PagosDetalleEvent.OnTogglePagoDialog) }) { Text("Cancelar", color = TextMuted) }
            },
            containerColor = CardBackground
        )
    }

    if (state.showAbonoDialog) {
        AlertDialog(
            onDismissRequest = { onEvent(PagosDetalleEvent.OnToggleAbonoDialog()) },
            title = { Text("Registrar Abono al Capital", fontWeight = FontWeight.Bold, color = TextDark) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Deuda actual de este cobro: $${state.selectedPagoParaAbono?.deuda ?: 0.0}", fontSize = 14.sp, color = TextDark, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = state.montoNuevoAbonoInput,
                        onValueChange = { onEvent(PagosDetalleEvent.OnMontoNuevoAbonoChanged(it)) },
                        label = { Text("Monto del Abono") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        isError = state.errorMessage != null,
                        supportingText = state.errorMessage?.let { {Text(it)} }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { onEvent(PagosDetalleEvent.OnRegistrarAbono) },
                    colors = ButtonDefaults.buttonColors(containerColor = HeaderOrange)
                ) { Text("Confirmar Abono", color = Color.White, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { onEvent(PagosDetalleEvent.OnToggleAbonoDialog()) }) { Text("Cancelar", color = TextMuted) }
            },
            containerColor = CardBackground
        )
    }

    if (state.showSaldarConfirmDialog && state.pagoParaSaldar != null) {
        val pago = state.pagoParaSaldar
        AlertDialog(
            onDismissRequest = { onEvent(PagosDetalleEvent.OnToggleSaldarConfirmDialog()) },
            title = { Text("Confirmar Saldo", fontWeight = FontWeight.Bold, color = TextDark) },
            text = {
                Text(
                    "¿Deseas marcar \"${pago.concepto}\" como pagado en su totalidad? Se registrará un abono de $${pago.deuda}.",
                    color = TextDark
                )
            },
            confirmButton = {
                Button(
                    onClick = { onEvent(PagosDetalleEvent.OnConfirmarSaldar) },
                    colors = ButtonDefaults.buttonColors(containerColor = HeaderOrange)
                ) { Text("Confirmar", color = Color.White, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { onEvent(PagosDetalleEvent.OnToggleSaldarConfirmDialog()) }) { Text("Cancelar", color = TextMuted) }
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
                            ) { Text(if (jugador.nombre.isNotEmpty()) jugador.nombre.take(1).uppercase() else "?", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = TextDark) }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(jugador.nombre.uppercase(), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TextDark)
                                Text("#${jugador.numeroCamiseta}, ${jugador.categoriaNombre.ifEmpty { "Sin Categoría" }}", fontSize = 13.sp, color = TextMuted)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- SECCIÓN SUSCRIPCIÓN ---
                state.jugador?.let { jugador ->
                    if (jugador.fechaVencimiento.isNotEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                            colors = CardDefaults.cardColors(containerColor = CardBackground),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, BorderColor)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("Estatus de Inscripción", fontWeight = FontWeight.Bold, color = PrimaryOrange, fontSize = 12.sp)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Plan ${jugador.tipoInscripcion}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                                        Text("Desde: ${jugador.fechaInicio}", fontSize = 12.sp, color = TextMuted)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text("Próximo Vencimiento", fontSize = 10.sp, color = TextMuted)
                                        Text(jugador.fechaVencimiento, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = HeaderOrange)
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Deuda Total Pendiente:", fontSize = 13.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("$${state.saldoPendiente}", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
                        Spacer(modifier = Modifier.height(12.dp))

                        val isAlDia = state.saldoPendiente <= 0
                        val statusBg = if (isAlDia) Color(0xFFE8F5E9) else BadgeUrgentBg
                        val statusText = if (isAlDia) "Al día" else "Con deudas pendientes"
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
                            Text("Fecha / Concepto", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, modifier = Modifier.weight(1.5f))
                            Text("Detalle Dinero", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, modifier = Modifier.weight(1f))
                            Text("Estado", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, textAlign = TextAlign.End, modifier = Modifier.weight(0.8f))
                        }
                        HorizontalDivider(color = DividerColor)
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(state.pagos) { pago ->
                                PagoItemRow(
                                    pago = pago,
                                    onMarcarPagado = { onEvent(PagosDetalleEvent.OnToggleSaldarConfirmDialog(pago)) },
                                    onAbonar = { onEvent(PagosDetalleEvent.OnToggleAbonoDialog(pago)) }
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
                        onClick = { onEvent(PagosDetalleEvent.OnTogglePagoDialog) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HeaderOrange)
                    ) { Text("Registrar Nuevo Pago / Abono", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White) }
                }
            }
        }
    }
}

@Composable
private fun PagoItemRow(pago: Pago, onMarcarPagado: () -> Unit, onAbonar: () -> Unit) {
    val isPagado = pago.estado.equals("PAGADO", ignoreCase = true)
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1.5f)) {
            Text(pago.fecha, fontSize = 11.sp, color = TextMuted)
            Spacer(modifier = Modifier.height(2.dp))
            Text(pago.concepto, fontSize = 13.sp, color = TextDark, fontWeight = FontWeight.Bold, maxLines = 2)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text("Total: $${pago.montoTotal}", fontSize = 11.sp, color = TextDark)
            Text("Abonado: $${pago.montoPagado}", fontSize = 11.sp, color = SuccessGreen)
            if (pago.deuda > 0) {
                Text("Debe: $${pago.deuda}", fontSize = 11.sp, color = Color(0xFFF44336), fontWeight = FontWeight.Bold)
            }
        }
        Row(modifier = Modifier.weight(0.8f), horizontalArrangement = Arrangement.End) {
            if (!isPagado) {
                Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(BadgeUrgentBg)
                            .clickable { onMarcarPagado() }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Saldar",
                            color = BadgeUrgentText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(HeaderOrange.copy(alpha = 0.1f))
                            .clickable { onAbonar() }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Abonar",
                            color = HeaderOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE8F5E9))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
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

@Preview(showBackground = true)
@Composable
fun PagosDetalleScreenPreview() {
    ProBasketAcademyTheme {
        PagosDetalleContent(
            state = PagosDetalleState(
                jugador = Jugador(nombre = "William Rodriguez", categoriaNombre = "U-20", numeroCamiseta = 24),
                saldoPendiente = 250.0,
                pagos = listOf(
                    Pago(concepto = "Cuota Mensual Enero", montoTotal = 500.0, montoPagado = 250.0, deuda = 250.0, estado = "ABONADO", fecha = "15 Ene 2026"),
                    Pago(concepto = "Inscripción", montoTotal = 300.0, montoPagado = 300.0, deuda = 0.0, estado = "PAGADO", fecha = "10 Ene 2026")
                )
            ),
            onEvent = {},
            onNavigateBack = {}
        )
    }
}