package com.probasketacademy.presentacion.categorias.asignar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.probasketacademy.domain.model.Jugador
import com.probasketacademy.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriaAsignarScreen(
    onNavigateBack: () -> Unit,
    viewModel: CategoriaAsignarViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) onNavigateBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Asignar Jugadores", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = HeaderOrange)
            )
        },
        bottomBar = {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                Button(
                    onClick = { viewModel.onEvent(CategoriaAsignarEvent.OnGuardarAsignacion) },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    enabled = state.seleccionados.isNotEmpty() && !state.isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HeaderOrange)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Guardar Asignación (${state.seleccionados.size})", fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        containerColor = LightBackground
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // Buscador
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { viewModel.onEvent(CategoriaAsignarEvent.OnSearchQueryChanged(it)) },
                modifier = Modifier.fillMaxWidth().padding(16.dp),
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

            // Lista
            LazyColumn(
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.jugadores) { jugador ->
                    val isSelected = state.seleccionados.contains(jugador.jugadorId)
                    JugadorSelectRow(
                        jugador = jugador,
                        isSelected = isSelected,
                        onToggle = { checked ->
                            viewModel.onEvent(CategoriaAsignarEvent.OnJugadorToggled(jugador.jugadorId, checked))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun JugadorSelectRow(jugador: Jugador, isSelected: Boolean, onToggle: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).background(BorderColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = jugador.nombre.take(1).uppercase(), fontWeight = FontWeight.Bold, color = TextDark)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = jugador.nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                Text(text = "Categoría Actual: ${jugador.categoriaNombre.ifEmpty { "Ninguna" }}", fontSize = 12.sp, color = TextMuted)
            }
            Checkbox(
                checked = isSelected,
                onCheckedChange = onToggle,
                colors = CheckboxDefaults.colors(checkedColor = HeaderOrange)
            )
        }
    }
}