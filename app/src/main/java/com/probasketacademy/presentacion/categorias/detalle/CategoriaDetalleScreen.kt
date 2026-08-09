package com.probasketacademy.presentacion.categorias.detalle

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
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
fun CategoriaDetalleScreen(
    categoriaId: Long,
    onNavigateBack: () -> Unit,
    viewModel: CategoriaDetalleViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(categoriaId) {
        viewModel.onEvent(CategoriaDetalleEvent.OnCargarDetalle(categoriaId))
    }

    // --- DIÁLOGO DE CONFIRMACIÓN DE GUARDADO ---
    if (state.showSaveSuccessDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(CategoriaDetalleEvent.OnShowSaveSuccessDialogChanged(false)) },
            title = { Text("Guardado Exitoso", fontWeight = FontWeight.Bold, color = TextDark) },
            text = { Text("La categoría se ha actualizado correctamente.", color = TextDark) },
            confirmButton = {
                Button(
                    onClick = { viewModel.onEvent(CategoriaDetalleEvent.OnShowSaveSuccessDialogChanged(false)) },
                    colors = ButtonDefaults.buttonColors(containerColor = HeaderOrange)
                ) {
                    Text("Aceptar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = CardBackground
        )
    }

    // --- DIÁLOGO DE CONFIRMACIÓN DE ELIMINACIÓN ---
    if (state.showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(CategoriaDetalleEvent.OnShowDeleteConfirmDialogChanged(false)) },
            title = { Text("Eliminar Categoría", fontWeight = FontWeight.Bold, color = TextDark) },
            text = { Text("¿Estás seguro de que deseas eliminar esta categoría? Los jugadores asignados quedarán sin categoría.", color = TextDark) },
            confirmButton = {
                Button(
                    onClick = { viewModel.onEvent(CategoriaDetalleEvent.OnEliminarCategoria) },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Eliminar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.onEvent(CategoriaDetalleEvent.OnShowDeleteConfirmDialogChanged(false)) }
                ) {
                    Text("Cancelar", color = TextMuted)
                }
            },
            containerColor = CardBackground
        )
    }

    // --- DIÁLOGO DE ÉXITO DE ELIMINACIÓN ---
    if (state.showDeleteSuccessDialog) {
        AlertDialog(
            onDismissRequest = {
                viewModel.onEvent(CategoriaDetalleEvent.OnShowDeleteSuccessDialogChanged(false))
                onNavigateBack()
            },
            title = { Text("Categoría Eliminada", fontWeight = FontWeight.Bold, color = TextDark) },
            text = { Text("La categoría ha sido eliminada correctamente.", color = TextDark) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.onEvent(CategoriaDetalleEvent.OnShowDeleteSuccessDialogChanged(false))
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HeaderOrange)
                ) {
                    Text("Aceptar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = CardBackground
        )
    }

    // --- DIÁLOGO PARA AGREGAR JUGADORES A LA CATEGORÍA ---
    if (state.showAddJugadoresDialog) {
        AlertDialog(
            onDismissRequest = { viewModel.onEvent(CategoriaDetalleEvent.OnShowAddJugadoresDialogChanged(false)) },
            title = {
                Text("Agregar Jugadores", fontWeight = FontWeight.Bold, color = TextDark)
            },
            text = {
                if (state.jugadoresSinCategoria.isEmpty()) {
                    Text("No hay jugadores sin categoría disponibles.", color = TextMuted)
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.jugadoresSinCategoria) { jugador ->
                            val isSelected = state.selectedJugadoresIds.contains(jugador.jugadorId)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.onEvent(CategoriaDetalleEvent.OnJugadorSelectionToggled(jugador.jugadorId))
                                    }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = {
                                        viewModel.onEvent(CategoriaDetalleEvent.OnJugadorSelectionToggled(jugador.jugadorId))
                                    },
                                    colors = CheckboxDefaults.colors(checkedColor = HeaderOrange)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = jugador.nombre,
                                    fontSize = 14.sp,
                                    color = TextDark,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.onEvent(CategoriaDetalleEvent.OnAsignarJugadoresSeleccionados) },
                    enabled = state.selectedJugadoresIds.isNotEmpty() && !state.isAssigning,
                    colors = ButtonDefaults.buttonColors(containerColor = HeaderOrange)
                ) {
                    if (state.isAssigning) {
                        CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Asignar (${state.selectedJugadoresIds.size})", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { viewModel.onEvent(CategoriaDetalleEvent.OnShowAddJugadoresDialogChanged(false)) }
                ) {
                    Text("Cancelar", color = TextMuted)
                }
            },
            containerColor = CardBackground
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle Categoría", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp) },
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
        if (state.isLoading || state.isDeleting) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = HeaderOrange)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- SECCIÓN EDITAR NOMBRE DE CATEGORÍA ---
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        shape = RoundedCornerShape(16.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Información de la Categoría",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextDark
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            OutlinedTextField(
                                value = state.nombreCategoria,
                                onValueChange = { viewModel.onEvent(CategoriaDetalleEvent.OnNombreCategoriaChanged(it)) },
                                label = { Text("Nombre de la categoría") },
                                isError = state.nombreError != null,
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryOrange,
                                    unfocusedBorderColor = BorderColor,
                                    focusedContainerColor = CardBackground,
                                    unfocusedContainerColor = CardBackground
                                )
                            )
                            state.nombreError?.let { error ->
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = error, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Button(
                                    onClick = { viewModel.onEvent(CategoriaDetalleEvent.OnGuardarNombreCategoria) },
                                    modifier = Modifier.weight(1f).height(44.dp),
                                    enabled = !state.isSavingNombre,
                                    colors = ButtonDefaults.buttonColors(containerColor = HeaderOrange),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    if (state.isSavingNombre) {
                                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                                    } else {
                                        Text("Guardar", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }
                                }
                                OutlinedButton(
                                    onClick = { viewModel.onEvent(CategoriaDetalleEvent.OnShowDeleteConfirmDialogChanged(true)) },
                                    modifier = Modifier.weight(1f).height(44.dp),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, Color.Red),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                                ) {
                                    Text("Eliminar", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                }
                            }
                        }
                    }
                }

                // --- SECCIÓN ENCABEZADO JUGADORES ASIGNADOS + BOTÓN AGREGAR ---
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Jugadores Asignados (${state.jugadoresAsignados.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextDark
                        )
                        Button(
                            onClick = { viewModel.onEvent(CategoriaDetalleEvent.OnShowAddJugadoresDialogChanged(true)) },
                            colors = ButtonDefaults.buttonColors(containerColor = HeaderOrange),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Agregar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // --- LISTA DE JUGADORES ASIGNADOS ---
                if (state.jugadoresAsignados.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No hay jugadores en esta categoría.", color = TextMuted, fontSize = 14.sp)
                        }
                    }
                } else {
                    items(state.jugadoresAsignados) { jugador ->
                        JugadorDetalleRow(
                            jugador = jugador,
                            onRemove = { viewModel.onEvent(CategoriaDetalleEvent.OnRemoverJugador(jugador)) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun JugadorDetalleRow(
    jugador: Jugador,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(IndicatorColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = jugador.nombre.take(1).uppercase(),
                    fontWeight = FontWeight.ExtraBold,
                    color = HeaderOrange
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = jugador.nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Remover", tint = Color.Red)
            }
        }
    }
}