package com.probasketacademy.presentacion.categorias.detalle

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.probasketacademy.domain.model.Jugador
import com.probasketacademy.ui.theme.*

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

    ManejoDeDialogosPrincipales(state, viewModel::onEvent, onNavigateBack)

    CategoriaDetalleContent(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoriaDetalleContent(
    state: CategoriaDetalleState,
    onEvent: (CategoriaDetalleEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    ManejoDeDialogosSecundarios(state, onEvent)

    Scaffold(
        topBar = { CategoriaTopBar(onNavigateBack) },
        containerColor = LightBackground
    ) { padding ->
        if (state.isLoading || state.isDeleting) {
            PantallaDeCarga()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                seccionEdicionCategoria(state, onEvent)
                seccionEncabezadoJugadores(state.jugadoresAsignados.size, onEvent)
                seccionListaJugadores(state, onEvent)
            }
        }
    }
}

@Composable
private fun ManejoDeDialogosPrincipales(
    state: CategoriaDetalleState,
    onEvent: (CategoriaDetalleEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    if (state.showDeleteSuccessDialog) {
        DialogoNotificacion(
            titulo = "Categoría Eliminada",
            mensaje = "La categoría ha sido eliminada correctamente.",
            onConfirm = {
                onEvent(CategoriaDetalleEvent.OnShowDeleteSuccessDialogChanged(false))
                onNavigateBack()
            }
        )
    }

    if (state.showRemoveConfirmDialog && state.jugadorParaRemover != null) {
        DialogoConfirmacion(
            titulo = "Remover Jugador",
            mensaje = "¿Deseas remover a ${state.jugadorParaRemover.nombre} de esta categoría?",
            btnConfirmarTexto = "Remover",
            colorConfirmar = Color.Red,
            onConfirm = { onEvent(CategoriaDetalleEvent.OnConfirmarRemoverJugador) },
            onCancel = { onEvent(CategoriaDetalleEvent.OnCancelarRemoverJugador) }
        )
    }
}

@Composable
private fun ManejoDeDialogosSecundarios(
    state: CategoriaDetalleState,
    onEvent: (CategoriaDetalleEvent) -> Unit
) {
    if (state.showSaveSuccessDialog) {
        DialogoNotificacion(
            titulo = "Guardado Exitoso",
            mensaje = "La categoría se ha actualizado correctamente.",
            onConfirm = { onEvent(CategoriaDetalleEvent.OnShowSaveSuccessDialogChanged(false)) }
        )
    }

    if (state.showDeleteConfirmDialog) {
        DialogoConfirmacion(
            titulo = "Eliminar Categoría",
            mensaje = "¿Estás seguro de que deseas eliminar esta categoría? Los jugadores asignados quedarán sin categoría.",
            btnConfirmarTexto = "Eliminar",
            colorConfirmar = Color.Red,
            onConfirm = { onEvent(CategoriaDetalleEvent.OnEliminarCategoria) },
            onCancel = { onEvent(CategoriaDetalleEvent.OnShowDeleteConfirmDialogChanged(false)) }
        )
    }

    if (state.showAddJugadoresDialog) {
        DialogoAgregarJugadores(state, onEvent)
    }
}

@Composable
private fun DialogoNotificacion(titulo: String, mensaje: String, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onConfirm,
        title = { Text(titulo, fontWeight = FontWeight.Bold, color = TextDark) },
        text = { Text(mensaje, color = TextDark) },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = HeaderOrange)) {
                Text("Aceptar", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = CardBackground
    )
}

@Composable
private fun DialogoConfirmacion(
    titulo: String,
    mensaje: String,
    btnConfirmarTexto: String,
    colorConfirmar: Color,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = { Text(titulo, fontWeight = FontWeight.Bold, color = TextDark) },
        text = { Text(mensaje, color = TextDark) },
        confirmButton = {
            Button(onClick = onConfirm, colors = ButtonDefaults.buttonColors(containerColor = colorConfirmar)) {
                Text(btnConfirmarTexto, color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onCancel) { Text("Cancelar", color = TextMuted) }
        },
        containerColor = CardBackground
    )
}

@Composable
private fun DialogoAgregarJugadores(state: CategoriaDetalleState, onEvent: (CategoriaDetalleEvent) -> Unit) {
    AlertDialog(
        onDismissRequest = { onEvent(CategoriaDetalleEvent.OnShowAddJugadoresDialogChanged(false)) },
        title = { Text("Agregar Jugadores", fontWeight = FontWeight.Bold, color = TextDark) },
        text = {
            if (state.jugadoresSinCategoria.isEmpty()) {
                Text("No hay jugadores sin categoría disponibles.", color = TextMuted)
            } else {
                ListaJugadoresSeleccionables(state, onEvent)
            }
        },
        confirmButton = {
            Button(
                onClick = { onEvent(CategoriaDetalleEvent.OnAsignarJugadoresSeleccionados) },
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
            TextButton(onClick = { onEvent(CategoriaDetalleEvent.OnShowAddJugadoresDialogChanged(false)) }) {
                Text("Cancelar", color = TextMuted)
            }
        },
        containerColor = CardBackground
    )
}

@Composable
private fun ListaJugadoresSeleccionables(state: CategoriaDetalleState, onEvent: (CategoriaDetalleEvent) -> Unit) {
    LazyColumn(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(state.jugadoresSinCategoria) { jugador ->
            val isSelected = state.selectedJugadoresIds.contains(jugador.jugadorId)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onEvent(CategoriaDetalleEvent.OnJugadorSelectionToggled(jugador.jugadorId)) }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onEvent(CategoriaDetalleEvent.OnJugadorSelectionToggled(jugador.jugadorId)) },
                    colors = CheckboxDefaults.colors(checkedColor = HeaderOrange)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = jugador.nombre, fontSize = 14.sp, color = TextDark, fontWeight = FontWeight.Medium)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoriaTopBar(onNavigateBack: () -> Unit) {
    TopAppBar(
        title = { Text("Detalle Categoría", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp) },
        navigationIcon = {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = HeaderOrange)
    )
}

@Composable
private fun PantallaDeCarga() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = HeaderOrange)
    }
}

private fun LazyListScope.seccionEdicionCategoria(state: CategoriaDetalleState, onEvent: (CategoriaDetalleEvent) -> Unit) {
    item {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = CardBackground),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Información de la Categoría", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = state.nombreCategoria,
                    onValueChange = { onEvent(CategoriaDetalleEvent.OnNombreCategoriaChanged(it)) },
                    label = { Text("Nombre de la categoría") },
                    isError = state.nombreError != null,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryOrange, unfocusedBorderColor = BorderColor,
                        focusedContainerColor = CardBackground, unfocusedContainerColor = CardBackground
                    )
                )

                if (state.nombreError != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = state.nombreError, color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))
                BotonesEdicionCategoria(state, onEvent)
            }
        }
    }
}

@Composable
private fun BotonesEdicionCategoria(state: CategoriaDetalleState, onEvent: (CategoriaDetalleEvent) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Button(
            onClick = { onEvent(CategoriaDetalleEvent.OnGuardarNombreCategoria) },
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
            onClick = { onEvent(CategoriaDetalleEvent.OnShowDeleteConfirmDialogChanged(true)) },
            modifier = Modifier.weight(1f).height(44.dp),
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, Color.Red),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
        ) {
            Text("Eliminar", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}

private fun LazyListScope.seccionEncabezadoJugadores(cantidad: Int, onEvent: (CategoriaDetalleEvent) -> Unit) {
    item {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Jugadores Asignados ($cantidad)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
            Button(
                onClick = { onEvent(CategoriaDetalleEvent.OnShowAddJugadoresDialogChanged(true)) },
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
}

private fun LazyListScope.seccionListaJugadores(state: CategoriaDetalleState, onEvent: (CategoriaDetalleEvent) -> Unit) {
    if (state.jugadoresAsignados.isEmpty()) {
        item {
            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                Text("No hay jugadores en esta categoría.", color = TextMuted, fontSize = 14.sp)
            }
        }
    } else {
        items(state.jugadoresAsignados) { jugador ->
            JugadorDetalleRow(
                jugador = jugador,
                onRemove = { onEvent(CategoriaDetalleEvent.OnSolicitarRemoverJugador(jugador)) }
            )
        }
    }
}

@Composable
private fun JugadorDetalleRow(jugador: Jugador, onRemove: () -> Unit) {
    val context = LocalContext.current
    val isActive = jugador.estado.equals("Activo", ignoreCase = true)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(IndicatorColor),
                contentAlignment = Alignment.Center
            ) {
                if (!jugador.fotoUri.isNullOrEmpty()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context).data(jugador.fotoUri).crossfade(true).build(),
                        contentDescription = "Foto", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop
                    )
                } else {
                    Text(text = jugador.nombre.take(1).uppercase(), fontWeight = FontWeight.ExtraBold, color = HeaderOrange)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = jugador.nombre, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
                Spacer(modifier = Modifier.height(2.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isActive) ChipActiveBg else ChipInactiveBg)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = jugador.estado, fontSize = 10.sp, fontWeight = FontWeight.Bold,
                        color = if (isActive) ChipActiveText else ChipInactiveText
                    )
                }
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Remover", tint = Color.Red)
            }
        }
    }
}
@Preview(showBackground = true)
@Composable
fun CategoriaDetalleScreenPreview() {
    ProBasketAcademyTheme {
        CategoriaDetalleContent(
            state = CategoriaDetalleState(
                nombreCategoria = "U-20",
                jugadoresAsignados = listOf(
                    Jugador(nombre = "William Rodriguez", estado = "Activo"),
                    Jugador(nombre = "Juan Perez", estado = "Inactivo")
                )
            ),
            onEvent = {},
            onNavigateBack = {}
        )
    }
}