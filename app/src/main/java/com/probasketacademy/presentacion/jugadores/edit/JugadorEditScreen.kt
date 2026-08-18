package com.probasketacademy.presentacion.jugadores.edit

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
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
fun JugadorEditScreen(
    jugadorId: Long,
    onNavigateBack: () -> Unit,
    viewModel: JugadorEditViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(jugadorId) {
        viewModel.cargarJugador(jugadorId)
    }

    LaunchedEffect(state.isSaved, state.isDeleted) {
        if (state.isSaved || state.isDeleted) {
            onNavigateBack()
        }
    }

    JugadorEditContent(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JugadorEditContent(
    state: JugadorEditState,
    onEvent: (JugadorEditEvent) -> Unit,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    val opcionesTallas = listOf("XXS", "XS", "S", "M", "L", "XL", "XXL")
    val opcionesVinculos = listOf("Padre", "Madre", "Tutor Legal", "Tío/a", "Abuelo/a", "Hermano/a Mayor")

    var categoriaExpanded by remember { mutableStateOf(false) }
    var tallaExpanded by remember { mutableStateOf(false) }
    var vinculoExpanded by remember { mutableStateOf(false) }

    val pickMedia = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            onEvent(JugadorEditEvent.OnFotoChanged(uri.toString()))
        }
    }

    val pickMediaActa = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            onEvent(JugadorEditEvent.OnActaNacimientoChanged(uri.toString()))
        }
    }

    var isEditing by remember(state.isNew) {
        mutableStateOf(state.isNew)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (isEditing) { if (state.isNew) "Nuevo Jugador" else "Editar Jugador" } else "Ficha del Jugador",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Atrás", tint = Color.White)
                    }
                },
                actions = {
                    if (!isEditing && !state.isNew) {
                        IconButton(onClick = { isEditing = true }) {
                            Icon(Icons.Default.Edit, contentDescription = "Editar", tint = Color.White)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = HeaderOrange)
            )
        },
        containerColor = LightBackground
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = HeaderOrange)
            }
        } else {
            if (isEditing) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(BorderColor)
                                .clickable {
                                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (!state.fotoUri.isNullOrEmpty()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(state.fotoUri)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Foto seleccionada",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.AddAPhoto,
                                        contentDescription = "Añadir foto",
                                        tint = TextMuted,
                                        modifier = Modifier.size(32.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("Añadir Foto", color = TextMuted, fontSize = 12.sp)
                                }
                            }
                        }
                    }

                    Text("Información Personal", fontWeight = FontWeight.Bold, color = PrimaryOrange)

                    OutlinedTextField(
                        value = state.nombre,
                        onValueChange = { onEvent(JugadorEditEvent.OnNombreChanged(it)) },
                        label = { Text("Nombre Completo") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        isError = state.nombreError != null,
                        supportingText = state.nombreError?.let { { Text(it) } }
                    )

                    OutlinedTextField(
                        value = state.domicilio,
                        onValueChange = { onEvent(JugadorEditEvent.OnDomicilioChanged(it)) },
                        label = { Text("Domicilio") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        isError = state.domicilioError != null,
                        supportingText = state.domicilioError?.let { { Text(it) } }
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = state.edad,
                            onValueChange = { onEvent(JugadorEditEvent.OnEdadChanged(it)) },
                            label = { Text("Edad") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            isError = state.edadError != null,
                            supportingText = state.edadError?.let { { Text(it) } }
                        )

                        OutlinedTextField(
                            value = state.telefono,
                            onValueChange = { onEvent(JugadorEditEvent.OnTelefonoChanged(it)) },
                            label = { Text("Teléfono") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            isError = state.telefonoError != null,
                            supportingText = state.telefonoError?.let { { Text(it) } }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Datos Deportivos", fontWeight = FontWeight.Bold, color = PrimaryOrange)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ExposedDropdownMenuBox(
                            expanded = categoriaExpanded,
                            onExpandedChange = { categoriaExpanded = !categoriaExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = state.categoriaNombre,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Categoría") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaExpanded) },
                                modifier = Modifier.menuAnchor(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = categoriaExpanded,
                                onDismissRequest = { categoriaExpanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Ninguna (Sin Categoría)", color = MaterialTheme.colorScheme.secondary) },
                                    onClick = {
                                        onEvent(JugadorEditEvent.OnCategoriaSelected(0L, "Sin Categoría"))
                                        categoriaExpanded = false
                                    }
                                )
                                state.categorias.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat.nombre) },
                                        onClick = {
                                            onEvent(JugadorEditEvent.OnCategoriaSelected(cat.id, cat.nombre))
                                            categoriaExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = state.numeroCamiseta,
                            onValueChange = { onEvent(JugadorEditEvent.OnNumeroCamisetaChanged(it)) },
                            label = { Text("No. Camiseta") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            isError = state.numeroCamisetaError != null,
                            supportingText = state.numeroCamisetaError?.let { { Text(it) } }
                        )
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = state.estatura,
                            onValueChange = { onEvent(JugadorEditEvent.OnEstaturaChanged(it)) },
                            label = { Text("Estatura (m)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            isError = state.estaturaError != null,
                            supportingText = state.estaturaError?.let { { Text(it) } }
                        )

                        OutlinedTextField(
                            value = state.peso,
                            onValueChange = { onEvent(JugadorEditEvent.OnPesoChanged(it)) },
                            label = { Text("Peso (kg)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            isError = state.pesoError != null,
                            supportingText = state.pesoError?.let { { Text(it) } }
                        )
                    }

                    ExposedDropdownMenuBox(
                        expanded = tallaExpanded,
                        onExpandedChange = { tallaExpanded = !tallaExpanded }
                    ) {
                        OutlinedTextField(
                            value = state.tallaCamiseta,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Talla de Camiseta") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tallaExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor(),
                            shape = RoundedCornerShape(12.dp),
                            isError = state.tallaCamisetaError != null,
                            supportingText = state.tallaCamisetaError?.let { { Text(it) } }
                        )
                        ExposedDropdownMenu(
                            expanded = tallaExpanded,
                            onDismissRequest = { tallaExpanded = false }
                        ) {
                            opcionesTallas.forEach { talla ->
                                DropdownMenuItem(
                                    text = { Text(talla) },
                                    onClick = {
                                        onEvent(JugadorEditEvent.OnTallaCamisetaChanged(talla))
                                        tallaExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Información del Tutor", fontWeight = FontWeight.Bold, color = PrimaryOrange)

                    OutlinedTextField(
                        value = state.tutorNombre,
                        onValueChange = { onEvent(JugadorEditEvent.OnTutorNombreChanged(it)) },
                        label = { Text("Nombre del Tutor") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        isError = state.tutorNombreError != null,
                        supportingText = state.tutorNombreError?.let { { Text(it) } }
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = state.tutorTelefono,
                            onValueChange = { onEvent(JugadorEditEvent.OnTutorTelefonoChanged(it)) },
                            label = { Text("Teléfono Tutor") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            isError = state.tutorTelefonoError != null,
                            supportingText = state.tutorTelefonoError?.let { { Text(it) } }
                        )

                        ExposedDropdownMenuBox(
                            expanded = vinculoExpanded,
                            onExpandedChange = { vinculoExpanded = !vinculoExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = state.tutorVinculo,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Vínculo") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = vinculoExpanded) },
                                modifier = Modifier.menuAnchor(),
                                shape = RoundedCornerShape(12.dp),
                                isError = state.tutorVinculoError != null,
                                supportingText = state.tutorVinculoError?.let { { Text(it) } }
                            )
                            ExposedDropdownMenu(
                                expanded = vinculoExpanded,
                                onDismissRequest = { vinculoExpanded = false }
                            ) {
                                opcionesVinculos.forEach { vinculo ->
                                    DropdownMenuItem(
                                        text = { Text(vinculo) },
                                        onClick = {
                                            onEvent(JugadorEditEvent.OnTutorVinculoChanged(vinculo))
                                            vinculoExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = state.tutorCorreo,
                        onValueChange = { onEvent(JugadorEditEvent.OnTutorCorreoChanged(it)) },
                        label = { Text("Correo del Tutor") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        isError = state.tutorCorreoError != null,
                        supportingText = state.tutorCorreoError?.let { { Text(it) } }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Estado y Documentación", fontWeight = FontWeight.Bold, color = PrimaryOrange)

                    Card(
                        colors = CardDefaults.cardColors(containerColor = CardBackground),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Jugador Activo", color = TextDark, fontWeight = FontWeight.Medium)
                                Switch(
                                    checked = state.estado.equals("Activo", ignoreCase = true),
                                    onCheckedChange = { isChecked ->
                                        onEvent(JugadorEditEvent.OnEstadoChanged(if (isChecked) "Activo" else "Inactivo"))
                                    },
                                    colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = SuccessGreen)
                                )
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = BorderColor)

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        pickMediaActa.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                    },
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(56.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(LightBackground),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (!state.actaNacimientoUri.isNullOrEmpty()) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(context)
                                                .data(state.actaNacimientoUri)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = "Acta de nacimiento",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    } else {
                                        Icon(
                                            imageVector = Icons.Default.AddAPhoto,
                                            contentDescription = "Subir acta de nacimiento",
                                            tint = TextMuted,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Acta de Nacimiento", color = TextDark, fontWeight = FontWeight.Medium)
                                    Text(
                                        text = if (!state.actaNacimientoUri.isNullOrEmpty())
                                            "Documento cargado"
                                        else
                                            "Toca para subir el documento",
                                        fontSize = 12.sp,
                                        color = if (!state.actaNacimientoUri.isNullOrEmpty()) SuccessGreen else TextMuted
                                    )
                                }
                                if (!state.actaNacimientoUri.isNullOrEmpty()) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = SuccessGreen,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { onEvent(JugadorEditEvent.OnGuardarClicked) },
                            enabled = !state.isSaving && !state.isDeleting,
                            modifier = Modifier.weight(1f).height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = HeaderOrange)
                        ) {
                            if (state.isSaving) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Guardar", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                            }
                        }

                        if (!state.isNew) {
                            Button(
                                onClick = { onEvent(JugadorEditEvent.OnEliminarClicked) },
                                enabled = !state.isSaving && !state.isDeleting,
                                modifier = Modifier.weight(1f).height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                if (state.isDeleting) {
                                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Eliminar", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color.White)
                                }
                            }
                        }
                    }

                    if (!state.isNew) {
                        OutlinedButton(
                            onClick = { isEditing = false },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancelar Edición", color = TextDark)
                        }
                    }
                }
            } else {
                // --- MODO LECTURA DE FICHA DEL JUGADOR ---
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(modifier = Modifier.padding(top = 16.dp)) {
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .clip(CircleShape)
                                .background(BorderColor),
                            contentAlignment = Alignment.Center
                        ) {
                            if (!state.fotoUri.isNullOrEmpty()) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(state.fotoUri)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = "Foto de perfil",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = if (state.nombre.isNotEmpty()) state.nombre.take(1).uppercase() else "?",
                                    fontSize = 40.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextMuted
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .offset(x = 4.dp, y = 4.dp)
                                .size(36.dp)
                                .background(HeaderOrange, CircleShape)
                                .border(2.dp, LightBackground, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "#${state.numeroCamiseta}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = state.nombre, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
                    Text(
                        text = "Talla: ${state.tallaCamiseta.ifEmpty { "No asignada" }}",
                        fontSize = 14.sp,
                        color = TextMuted
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val isActive = state.estado.equals("Activo", ignoreCase = true)
                        AssistChip(
                            onClick = { },
                            label = { Text(state.estado, color = if (isActive) ActiveBadgeText else TextMuted, fontWeight = FontWeight.Bold) },
                            leadingIcon = {
                                Box(modifier = Modifier.size(8.dp).background(if (isActive) ActiveBadgeText else TextMuted, CircleShape))
                            },
                            colors = AssistChipDefaults.assistChipColors(containerColor = if (isActive) ActiveBadgeBg else ChipInactiveBg),
                            border = null
                        )
                        AssistChip(
                            onClick = { },
                            label = { Text(if (state.docCompleta) "Doc. Completa" else "Doc. Pendiente", color = TextDark) },
                            leadingIcon = {
                                if (state.docCompleta) Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            InfoCard(modifier = Modifier.weight(1f), title = "CATEGORÍA", value = state.categoriaNombre.ifEmpty { "N/A" }, subtitle = "Asignada")
                            InfoCard(modifier = Modifier.weight(1f), title = "FÍSICO", value = "${state.estatura} m", subtitle = "${state.peso} kg")
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            InfoCard(modifier = Modifier.weight(1f), title = "EDAD", value = "${state.edad} años", subtitle = "Registrada")
                            InfoCard(modifier = Modifier.weight(1f), title = "TELÉFONO", value = state.telefono.ifEmpty { "N/A" }, subtitle = "Personal")
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text("Ubicación", fontWeight = FontWeight.Bold, color = PrimaryOrange, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CardBackground),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextMuted, modifier = Modifier.size(22.dp))
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(text = state.domicilio.ifEmpty { "No registrado" }, fontSize = 15.sp, color = TextDark)
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text("Información del Tutor / Responsable", fontWeight = FontWeight.Bold, color = PrimaryOrange, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CardBackground),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier.size(40.dp).background(IndicatorColor, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Person, contentDescription = null, tint = HeaderOrange, modifier = Modifier.size(20.dp))
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(text = state.tutorNombre.ifEmpty { "No registrado" }, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                                        Text(text = state.tutorVinculo.ifEmpty { "Vínculo no definido" }, fontSize = 13.sp, color = TextMuted)
                                    }
                                }

                                HorizontalDivider(color = DividerColor)

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(text = state.tutorTelefono.ifEmpty { "No registrado" }, fontSize = 15.sp, color = TextDark)
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Email, contentDescription = null, tint = TextMuted, modifier = Modifier.size(20.dp))
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(text = state.tutorCorreo.ifEmpty { "No registrado" }, fontSize = 15.sp, color = TextDark)
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text("Documentación", fontWeight = FontWeight.Bold, color = PrimaryOrange, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = CardBackground),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text("Acta de Nacimiento", fontSize = 13.sp, color = TextMuted, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(8.dp))
                                if (!state.actaNacimientoUri.isNullOrEmpty()) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(state.actaNacimientoUri)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Acta de nacimiento",
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(200.dp)
                                            .clip(RoundedCornerShape(12.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(120.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(LightBackground),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = TextMuted, modifier = Modifier.size(28.dp))
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text("Documento no cargado", color = TextMuted, fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
private fun InfoCard(modifier: Modifier = Modifier, title: String, value: String, subtitle: String) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextMuted, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = value, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
            Text(text = subtitle, fontSize = 12.sp, color = TextMuted)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun JugadorEditScreenPreview() {
    ProBasketAcademyTheme {
        JugadorEditContent(
            state = JugadorEditState(
                isNew = false,
                nombre = "William Rodriguez",
                edad = "20",
                estatura = "1.85",
                peso = "80",
                categoriaNombre = "U-20",
                numeroCamiseta = "24"
            ),
            onEvent = {},
            onNavigateBack = {}
        )
    }
}