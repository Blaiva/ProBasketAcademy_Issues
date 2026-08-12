package com.probasketacademy.presentacion.finanzas.list

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.probasketacademy.R
import com.probasketacademy.domain.model.Jugador
import com.probasketacademy.presentacion.finanzas.list.PagosListEvent
import com.probasketacademy.ui.theme.*

@Composable
fun PagosListScreen(
    onNavigateToDetalle: (Long) -> Unit,
    viewModel: PagosListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(containerColor = LightBackground) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // ENCABEZADO ESTÁNDAR
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
                    Box(
                        modifier = Modifier.size(36.dp).background(Color.White.copy(alpha = 0.2f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) { Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp)) }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TÍTULO Y BUSCADOR
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text("Finanzas y Pagos", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Selecciona un jugador para gestionar sus cuotas.", fontSize = 13.sp, color = TextMuted)

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onEvent(PagosListEvent.OnSearchQueryChanged(it)) },
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

            // LISTA DE JUGADORES
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
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier.size(48.dp).background(BorderColor, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) { Text(jugador.nombre.take(1).uppercase(), fontWeight = FontWeight.Bold, color = TextDark, fontSize = 18.sp) }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(jugador.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                                    Text("${jugador.categoriaNombre}   #${jugador.numeroCamiseta}", fontSize = 12.sp, color = TextMuted)
                                }
                                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = ChevronColor)
                            }
                        }
                    }
                }
            }
        }
    }
}