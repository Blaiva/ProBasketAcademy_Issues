package com.probasketacademy.presentacion.pagos.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.probasketacademy.R
import com.probasketacademy.domain.model.Jugador
import com.probasketacademy.presentacion.perfil.ProfileDialog
import com.probasketacademy.ui.theme.*

@Composable
fun PagosListScreen(
    onNavigateToDetalle: (Long) -> Unit,
    onLogout: () -> Unit,
    viewModel: PagosListViewModel = hiltViewModel()
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

    PagosListContent(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateToDetalle = onNavigateToDetalle,
        onProfileClick = { showProfileDialog = true }
    )
}

@Composable
fun PagosListContent(
    state: PagosListState,
    onEvent: (PagosListEvent) -> Unit,
    onNavigateToDetalle: (Long) -> Unit,
    onProfileClick: () -> Unit
) {
    Scaffold(containerColor = LightBackground) { padding ->
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
                            modifier = Modifier.size(36.dp).background(Color.White, CircleShape).padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) { Image(painter = painterResource(id = R.drawable.logo_probasket), contentDescription = "Logo", modifier = Modifier.fillMaxSize()) }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("ProBasketAcademy", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }

                    val isPreview = androidx.compose.ui.platform.LocalInspectionMode.current
                    val photoUrl = if (!isPreview) {
                        com.google.firebase.auth.FirebaseAuth.getInstance().currentUser?.photoUrl?.toString()
                    } else null

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable { onProfileClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        if (!photoUrl.isNullOrEmpty()) {
                            coil3.compose.AsyncImage(
                                model = photoUrl,
                                contentDescription = "Perfil",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Perfil",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Finanzas y Pagos", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Monitorea los ingresos de la academia y las deudas.", fontSize = 13.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = HeaderOrange.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        FinancialColumn(label = "Generado", amount = state.totalGeneradoGlobal, color = TextDark)
                        FinancialColumn(label = "Pagado", amount = state.totalPagadoGlobal, color = Color(0xFF4CAF50))
                        FinancialColumn(label = "Deuda Global", amount = state.deudaGlobal, color = if (state.deudaGlobal > 0) Color(0xFFF44336) else TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { onEvent(PagosListEvent.OnSearchQueryChanged(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Buscar jugador...", color = TextMuted) },
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
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = HeaderOrange) }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.jugadores) { jugador ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { onNavigateToDetalle(jugador.jugadorId) },
                            colors = CardDefaults.cardColors(containerColor = CardBackground),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
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
                                                model = ImageRequest.Builder(LocalContext.current)
                                                    .data(jugador.fotoUri)
                                                    .crossfade(true)
                                                    .build(),
                                                contentDescription = "Foto de ${jugador.nombre}",
                                                modifier = Modifier.fillMaxSize(),
                                                contentScale = ContentScale.Crop
                                            )
                                        } else {
                                            Text(
                                                if (jugador.nombre.isNotEmpty()) jugador.nombre.take(1).uppercase() else "?",
                                                fontWeight = FontWeight.Bold, color = TextDark, fontSize = 18.sp
                                            )
                                        }
                                    }
                                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = ChevronColor)
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                HorizontalDivider(color = BorderColor, thickness = 0.5.dp)
                                Spacer(modifier = Modifier.height(12.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    FinancialColumn(label = "Generado", amount = jugador.totalGenerado, color = TextMuted)
                                    FinancialColumn(label = "Pagado", amount = jugador.totalPagado, color = Color(0xFF4CAF50))
                                    FinancialColumn(label = "Deuda", amount = jugador.deudaActual, color = if (jugador.deudaActual > 0) Color(0xFFF44336) else TextMuted)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FinancialColumn(label: String, amount: Double, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = label, fontSize = 11.sp, color = TextMuted)
        Text(
            text = "$$amount",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PagosListPreview() {
    ProBasketAcademyTheme {
        PagosListContent(
            state = PagosListState(
                jugadores = listOf(
                    Jugador(nombre = "William Rodriguez", categoriaNombre = "U-20", totalGenerado = 500.0, totalPagado = 250.0, deudaActual = 250.0)
                ),
                totalGeneradoGlobal = 500.0,
                totalPagadoGlobal = 250.0,
                deudaGlobal = 250.0
            ),
            onEvent = {},
            onNavigateToDetalle = {},
            onProfileClick = {}
        )
    }
}