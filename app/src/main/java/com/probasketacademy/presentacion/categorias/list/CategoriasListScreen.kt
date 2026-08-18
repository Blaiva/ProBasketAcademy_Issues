package com.probasketacademy.presentacion.categorias.list

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.probasketacademy.R
import com.probasketacademy.domain.model.Categoria
import com.probasketacademy.presentacion.perfil.ProfileDialog
import com.probasketacademy.ui.theme.*

@Composable
fun CategoriasListScreen(
    onNavigateToVerEditar: (Long) -> Unit,
    onLogout: () -> Unit,
    viewModel: CategoriasListViewModel = hiltViewModel()
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

    CategoriasListContent(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateToVerEditar = onNavigateToVerEditar,
        onProfileClick = { showProfileDialog = true }
    )
}

@Composable
fun CategoriasListContent(
    state: CategoriasListState,
    onEvent: (CategoriasListEvent) -> Unit,
    onNavigateToVerEditar: (Long) -> Unit,
    onProfileClick: () -> Unit
) {
    if (state.showDialog) {
        AlertDialog(
            onDismissRequest = { onEvent(CategoriasListEvent.OnShowDialogChanged(false)) },
            title = { Text("Nueva Categoría", fontWeight = FontWeight.Bold, color = TextDark) },
            text = {
                Column {
                    OutlinedTextField(
                        value = state.nombreCategoria,
                        onValueChange = { onEvent(CategoriasListEvent.OnNombreCategoriaChanged(it)) },
                        label = { Text("Nombre de la categoría") },
                        isError = state.nombreError != null,
                        singleLine = true,
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
                }
            },
            confirmButton = {
                Button(
                    onClick = { onEvent(CategoriasListEvent.OnGuardarCategoria) },
                    enabled = !state.isSaving,
                    colors = ButtonDefaults.buttonColors(containerColor = HeaderOrange)
                ) {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { onEvent(CategoriasListEvent.OnShowDialogChanged(false)) }
                ) {
                    Text("Cancelar", color = TextMuted)
                }
            },
            containerColor = CardBackground
        )
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onEvent(CategoriasListEvent.OnShowDialogChanged(true)) },
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
                .padding(bottom = padding.calculateBottomPadding())
        ) {
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
                            onClick = { onNavigateToVerEditar(categoria.id) }
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
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(IndicatorColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Groups, contentDescription = null, tint = HeaderOrange)
                }
                Spacer(modifier = Modifier.width(16.dp))
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
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CategoriasListScreenPreview() {
    ProBasketAcademyTheme {
        CategoriasListContent(
            state = CategoriasListState(
                categorias = listOf(
                    Categoria(nombre = "U-20", totalJugadores = 15),
                    Categoria(nombre = "U-18", totalJugadores = 12)
                )
            ),
            onEvent = {},
            onNavigateToVerEditar = {},
            onProfileClick = {}
        )
    }
}