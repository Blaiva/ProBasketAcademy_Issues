package com.probasketacademy.presentacion.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.probasketacademy.R
import com.probasketacademy.domain.model.Pago
import com.probasketacademy.presentacion.navegacion.Screen
import com.probasketacademy.ui.theme.*

@Composable
fun HomeScreen(
    onNavigateTo: (Screen) -> Unit,
    onLogout: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LightBackgroundHome)
    ) {
        HomeHeader(onLogout)

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 24.dp, bottom = 32.dp)
        ) {
            item {
                HomeTitleSection()
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
                StatCard(
                    title = "JUGADORES ACTIVOS",
                    value = state.jugadoresActivos.toString(),
                    trendText = "Registrados en el sistema",
                    trendColor = TextMuted,
                    icon = Icons.Default.Group,
                    iconColor = PrimaryOrange
                )
                Spacer(modifier = Modifier.height(12.dp))
                StatCard(
                    title = "ASISTENCIA PROMEDIO",
                    value = state.asistenciaPromedio,
                    trendText = "Este mes",
                    trendColor = TextMuted,
                    icon = Icons.Default.CalendarMonth,
                    iconColor = BlueIcon
                )
                Spacer(modifier = Modifier.height(12.dp))
                StatCard(
                    title = "INGRESOS TOTALES",
                    value = state.ingresosMes,
                    trendText = "Pagos recolectados",
                    trendColor = SuccessGreen,
                    icon = Icons.Default.Payments,
                    iconColor = GreenTrend
                )
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
                FinancialChartCard()
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
                PendingPaymentsSection(pagos = state.cobrosPendientes)
            }
        }
    }
}

@Composable
private fun HomeHeader(onLogout: () -> Unit) {
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
                Text(
                    text = "ProBasketAcademy",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.White
                )
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
                    .clip(CircleShape)
                    .clickable { onLogout() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Cerrar Sesión",
                    tint = Color.White,
                    modifier = Modifier.size(20.dp)
                )
            }
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                val heights = listOf(0.4f, 0.6f, 0.5f, 1.0f, 0.7f, 0.8f)
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
                if (pagos.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .background(BadgeUrgentBg, RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Urgente",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BadgeUrgentText
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (pagos.isEmpty()) {
                Text(
                    text = "¡Todo está al día! No hay cobros pendientes.",
                    color = SuccessGreen,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                )
            } else {
                pagos.take(5).forEach { pago ->
                    PendingPaymentItemRow(pago)
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = DividerColor)
                }
                OutlinedButton(
                    onClick = { /* Navegar a una vista de todos los cobros a futuro */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text("Ver todos los pendientes (${pagos.size})", color = TextDark, fontWeight = FontWeight.Medium)
                }
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
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(DividerColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = initials, fontWeight = FontWeight.Bold, color = TextDark, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = pago.jugadorNombre, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
            Text(text = pago.concepto, fontSize = 12.sp, color = TextMuted)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "$${pago.montoTotal.toInt()}",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = PrimaryOrange
            )
            Text(text = pago.fecha, fontSize = 10.sp, color = TextMuted)
        }
    }
}