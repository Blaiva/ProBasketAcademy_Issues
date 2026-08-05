package com.probasketacademy.presentacion.categorias.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.probasketacademy.R
import com.probasketacademy.domain.model.Categoria
import com.probasketacademy.ui.theme.*

@Composable
fun CategoriasListScreen(
    onNavigateToAsignarJugador: (Long) -> Unit,
    onNavigateToVerEditar: (Long) -> Unit,
    onAddCategoria: () -> Unit, // Lo mantenemos para no romper la navegación en ApNavDisplay
    viewModel: CategoriasListViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    // Variables de estado para controlar el cuadro de diálogo
    var showDialog by remember { mutableStateOf(false) }
    var newCategoriaName by remember { mutableStateOf("") }

    // --- CUADRO DE DIÁLOGO PARA CREAR CATEGORÍA ---
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Nueva Categoría", fontWeight = FontWeight.Bold, color = TextDark) },
            text = {
                OutlinedTextField(
                    value = newCategoriaName,
                    onValueChange = { newCategoriaName = it },
                    label = { Text("Nombre de la categoría") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryOrange,
                        unfocusedBorderColor = BorderColor,
                        focusedContainerColor = CardBackground,
                        unfocusedContainerColor = CardBackground
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCategoriaName.isNotBlank()) {
                            viewModel.onEvent(CategoriasListEvent.OnGuardarCategoria(newCategoriaName))
                            showDialog = false
                            newCategoriaName = "" // Limpiamos el texto
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HeaderOrange)
                ) {
                    Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    newCategoriaName = ""
                }) {
                    Text("Cancelar", color = TextMuted)
                }
            },
            containerColor = CardBackground
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    showDialog = true // Mostramos el diálogo en lugar de navegar
                },
                containerColor = HeaderOrange,
                contentColor = Color.White,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Categoría")
            }
        },
        containerColor = LightBackground
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // --- NUEVO ENCABEZADO CON LOGO Y PERFIL ---
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

            // Título
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(
                    text = "Categorías\nAdministradas",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextDark,
                    lineHeight = 34.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Gestiona equipos, asigna entrenadores y revisa el progreso general.",
                    fontSize = 13.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Lista
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = HeaderOrange)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.categorias) { categoria ->
                        CategoriaItemCard(
                            categoria = categoria,
                            onAsignarClick = {
                                viewModel.onEvent(CategoriasListEvent.OnAsignarJugadorClicked(categoria.id))
                                onNavigateToAsignarJugador(categoria.id)
                            },
                            onVerEditarClick = {
                                viewModel.onEvent(CategoriasListEvent.OnVerEditarClicked(categoria.id))
                                onNavigateToVerEditar(categoria.id)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoriaItemCard(
    categoria: Categoria,
    onAsignarClick: () -> Unit,
    onVerEditarClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Ícono
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(IndicatorColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Groups, contentDescription = null, tint = HeaderOrange)
                }
                Spacer(modifier = Modifier.width(16.dp))
                // Textos
                Column {
                    Text(
                        text = categoria.nombre,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 18.sp,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(ActiveBadgeBg)
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${categoria.totalJugadores} Jugadores",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = ActiveBadgeText
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Gestionar roster",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onAsignarClick,
                    modifier = Modifier.weight(1f).height(40.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = HeaderOrange),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Asignar Jugador", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                OutlinedButton(
                    onClick = onVerEditarClick,
                    modifier = Modifier.weight(1f).height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(HeaderOrange)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = HeaderOrange)
                ) {
                    Text("Ver/Editar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}