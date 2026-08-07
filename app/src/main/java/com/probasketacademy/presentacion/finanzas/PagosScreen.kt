package com.probasketacademy.presentacion.finanzas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.probasketacademy.R
import com.probasketacademy.domain.model.Pago
import com.probasketacademy.ui.theme.*


@Composable
fun PagosScreen(
    viewModel: PagosViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

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

            // --- TÍTULO ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Estado de\nCuenta Jugador",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextDark,
                    lineHeight = 34.sp
                )
                IconButton(onClick = { /* Todo: Filtro */ }) {
                    Icon(Icons.Default.FilterList, contentDescription = "Filtrar", tint = TextDark)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = HeaderOrange)
                }
            } else {
                // --- TARJETA DEL JUGADOR ---
                state.jugador?.let { jugador ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, BorderColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .background(LightBackground, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = jugador.nombre.take(1).uppercase(),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp,
                                    color = TextDark
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = jugador.nombre.uppercase(), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TextDark)
                                Text(text = "#${jugador.numeroCamiseta}, ${jugador.categoriaNombre.ifEmpty { "Sin Categoría" }}", fontSize = 13.sp, color = TextMuted)
                            }
                            // NOTA: El botón de llamada fue removido por solicitud explícita
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- TARJETA DE SALDO ---
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Saldo Pendiente:", fontSize = 13.sp, color = TextMuted)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "€${state.saldoPendiente.toInt()}", fontSize = 36.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)

                        Spacer(modifier = Modifier.height(12.dp))

                        val isAlDia = state.saldoPendiente <= 0
                        val statusBg = if (isAlDia) Color(0xFFE8F5E9) else BadgeUrgentBg
                        val statusText = if (isAlDia) "Al día" else "Con deudas"
                        val statusColor = if (isAlDia) SuccessGreen else BadgeUrgentText

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(statusBg)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = statusColor, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = statusText, color = statusColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- LISTA DE PAGOS ---
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Column {
                        // Cabecera de la tabla
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(LightBackground)
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Fecha", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, modifier = Modifier.weight(1f))
                            Text(text = "Concepto", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, modifier = Modifier.weight(1.5f))
                            Text(text = "Monto/Est", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextMuted, textAlign = TextAlign.End, modifier = Modifier.weight(1f))
                        }
                        HorizontalDivider(color = DividerColor)

                        // Filas
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(state.pagos) { pago ->
                                PagoItemRow(pago)
                                HorizontalDivider(color = DividerColor)
                            }
                        }
                    }
                }

                // --- BOTONES INFERIORES ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(CardBackground)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { viewModel.onEvent(PagosEvent.OnEnviarRecordatorio) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = HeaderOrange)
                    ) {
                        Text("Enviar Recordatorio", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                    }

                    Button(
                        onClick = { viewModel.onEvent(PagosEvent.OnRegistrarPago) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DividerColor)
                    ) {
                        Text("Registrar Pago", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                    }
                }
            }
        }
    }
}

@Composable
private fun PagoItemRow(pago: Pago) {
    val isPending = pago.estado == "PENDIENTE"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Fecha
        Text(
            text = pago.fecha,
            fontSize = 12.sp,
            color = TextDark,
            modifier = Modifier.weight(1f)
        )

        // Concepto
        Text(
            text = pago.concepto,
            fontSize = 13.sp,
            color = TextDark,
            modifier = Modifier.weight(1.5f),
            maxLines = 2
        )

        // Monto o Estado
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isPending) {
                Box(modifier = Modifier.size(6.dp).background(BadgeUrgentText, CircleShape))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "Pendiente", color = BadgeUrgentText, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            } else {
                if (pago.monto > 0) {
                    Text(text = "€${pago.monto}", color = TextDark, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                } else {
                    Box(modifier = Modifier.size(6.dp).background(SuccessGreen, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Pagado", color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}