package com.probasketacademy.presentacion.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.probasketacademy.domain.model.Pago

// Colores basados en tu AuthScreen y tu diseño
private val PrimaryOrange = Color(0xFFE5634D)
private val LightBackground = Color(0xFFF8F9FA)
private val CardBackground = Color.White
private val TextDark = Color(0xFF1E293B)
private val TextMuted = Color(0xFF94A3B8)
private val GreenTrend = Color(0xFF22C55E)
private val ChartBarLight = Color(0xFFE2C9B8)
private val ChartBarDark = Color(0xFFB34A1B)

@Composable
fun HomeScreen(
    onNavigateTo: (Any) -> Unit,
    onLogout: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackground)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {
        // Top Bar
        item { HomeHeader(onLogout) }

        // Título y Botón de Acción
        item {
            Spacer(modifier = Modifier.height(24.dp))
            HomeTitleSection()
        }

        // Tarjetas de Estadísticas
        item {
            Spacer(modifier = Modifier.height(24.dp))
            StatCard(
                title = "JUGADORES ACTIVOS",
                value = state.jugadoresActivos.toString(),
                trendText = "+12 este mes",
                trendColor = GreenTrend,
                icon = Icons.Default.Group,
                iconColor = PrimaryOrange
            )
            Spacer(modifier = Modifier.height(12.dp))
            StatCard(
                title = "ASISTENCIA PROMEDIO",
                value = state.asistenciaPromedio,
                trendText = "Estable",
                trendColor = TextMuted,
                icon = Icons.Default.CalendarMonth,
                iconColor = Color(0xFF4A90E2)
            )
            Spacer(modifier = Modifier.height(12.dp))
            StatCard(
                title = "INGRESOS JUL",
                value = state.ingresosMes,
                trendText = "+15% vs mes anterior",
                trendColor = GreenTrend,
                icon = Icons.Default.Payments,
                iconColor = GreenTrend
            )
        }

        // Gráfica de Finanzas
        item {
            Spacer(modifier = Modifier.height(24.dp))
            FinancialChartCard()
        }

        // Lista de Pendientes por Cobrar
        item {
            Spacer(modifier = Modifier.height(24.dp))
            PendingPaymentsSection(pagos = state.cobrosPendientes)
        }
    }
}

@Composable
private fun HomeHeader(onLogout: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(PrimaryOrange, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder para el logo del balón
                Canvas(modifier = Modifier.size(16.dp)) {
                    drawCircle(color = Color.White)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "ProBasketAcademy",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = TextDark
            )
        }

        IconButton(
            onClick = onLogout,
            modifier = Modifier
                .size(36.dp)
                .background(Color(0xFFE2E8F0), CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Perfil / Cerrar Sesión",
                tint = TextDark,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun HomeTitleSection() {
    Column {
        Text(
            text = "Panel de control",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = TextDark
        )
        Text(
            text = "Resumen general de la academia",
            fontSize = 14.sp,
            color = TextMuted
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { /* Acción para Nueva Acción */ },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
            shape = RoundedCornerShape(24.dp),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Nueva Acción", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String,
    trendText: String,
    trendColor: Color,
    icon: ImageVector,
    iconColor: Color
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted,
                    letterSpacing = 1.sp
                )
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(iconColor.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = TextDark
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = trendText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = trendColor
                )
            }
        }
    }
}

@Composable
private fun FinancialChartCard() {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Evolución de Finanzas",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Icon(Icons.Default.MoreHoriz, contentDescription = null, tint = TextMuted)
            }
            Spacer(modifier = Modifier.height(24.dp))

            // Simulación de Gráfica de barras
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                val heights = listOf(0.4f, 0.6f, 0.5f, 1.0f, 0.7f, 0.8f) // Porcentajes de altura
                heights.forEachIndexed { index, heightFactor ->
                    Box(
                        modifier = Modifier
                            .width(32.dp)
                            .fillMaxHeight(heightFactor)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(if (index == 3) ChartBarDark else ChartBarLight)
                    )
                }
            }
        }
    }
}

@Composable
private fun PendingPaymentsSection(pagos: List<Pago>) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Pendientes por Cobrar",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark,
                    modifier = Modifier.weight(1f)
                )
                Box(
                    modifier = Modifier
                        .background(Color(0xFFFFE4E6), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Urgente",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFE11D48)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            pagos.forEach { pago ->
                PendingPaymentItemRow(pago)
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))
            }

            OutlinedButton(
                onClick = { /* Ver todos */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                shape = RoundedCornerShape(12.dp),
            ) {
                Text("Ver todos", color = TextDark, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@Composable
private fun PendingPaymentItemRow(pago: Pago) {
    val initials = pago.jugadorNombre.split(" ").take(2).joinToString("") { it.take(1).uppercase() }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar con Iniciales
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFF1F5F9), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = initials, fontWeight = FontWeight.Bold, color = TextDark, fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Detalles
        Column(modifier = Modifier.weight(1f)) {
            Text(text = pago.jugadorNombre, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
            Text(text = pago.concepto, fontSize = 12.sp, color = TextMuted)
        }

        // Monto y Fecha
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "€${pago.monto.toInt()}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = PrimaryOrange
            )
            Text(text = pago.fecha, fontSize = 10.sp, color = TextMuted)
        }
    }
}