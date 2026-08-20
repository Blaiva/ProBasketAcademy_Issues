package com.probasketacademy.presentacion.asistencias

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.google.firebase.auth.FirebaseAuth
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.probasketacademy.R
import com.probasketacademy.domain.model.Jugador
import com.probasketacademy.presentacion.perfil.ProfileDialog
import com.probasketacademy.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun AsistenciasScreen(
    onLogout: () -> Unit,
    viewModel: AsistenciasViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showProfileDialog by remember { mutableStateOf(false) }

    LaunchedEffect(state.isSaved) {
        if (state.isSaved) {
            viewModel.onEvent(AsistenciasEvent.OnResetGuardado)
        }
    }

    if (showProfileDialog) {
        ProfileDialog(
            user = FirebaseAuth.getInstance().currentUser,
            onDismiss = { showProfileDialog = false },
            onLogout = { showProfileDialog = false; onLogout() }
        )
    }

    if (state.showQuitarConfirmDialog && state.jugadorParaQuitar != null) {
        QuitarAsistenciaDialog(
            jugador = state.jugadorParaQuitar!!,
            onConfirmar = { viewModel.onEvent(AsistenciasEvent.OnConfirmarQuitarAsistencia) },
            onCancelar = { viewModel.onEvent(AsistenciasEvent.OnCancelarQuitarAsistencia) }
        )
    }

    AsistenciasContent(
        state = state,
        onEvent = viewModel::onEvent,
        onProfileClick = { showProfileDialog = true }
    )
}
@Composable
fun AsistenciasContent(
    state: AsistenciasState,
    onEvent: (AsistenciasEvent) -> Unit,
    onProfileClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val currentMonth = remember { YearMonth.now() }
    val calendarState = rememberCalendarState(
        startMonth = currentMonth.minusMonths(24),
        endMonth = currentMonth.plusMonths(24),
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = remember { firstDayOfWeekFromLocale() }
    )
    val visibleMonth = calendarState.firstVisibleMonth.yearMonth

    LaunchedEffect(visibleMonth) {
        onEvent(AsistenciasEvent.OnVisibleMonthChanged(visibleMonth))
    }

    Scaffold(containerColor = LightBackground) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item {
                HeaderSection(onProfileClick)
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                CategoriaSelector(state, onEvent)
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (state.categoriaSeleccionadaId == null) {
                item { MensajeCategoriaVacia() }
            } else {
                item {
                    CalendarioSection(calendarState, visibleMonth, state, onEvent, coroutineScope)
                    Spacer(modifier = Modifier.height(24.dp))
                }

                construirSeccionJugadores(state, onEvent)
            }
        }
    }
}
@Composable
private fun QuitarAsistenciaDialog(jugador: Jugador, onConfirmar: () -> Unit, onCancelar: () -> Unit) {
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Quitar Asistencia", fontWeight = FontWeight.Bold, color = TextDark) },
        text = { Text("¿Deseas quitar la asistencia registrada de ${jugador.nombre} para hoy?", color = TextDark) },
        confirmButton = {
            Button(
                onClick = onConfirmar,
                colors = ButtonDefaults.buttonColors(containerColor = HeaderOrange)
            ) { Text("Quitar", color = Color.White, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar", color = TextMuted) }
        },
        containerColor = CardBackground
    )
}

@Composable
private fun HeaderSection(onProfileClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(HeaderOrange, RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp))
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(36.dp).background(Color.White, CircleShape).padding(4.dp), contentAlignment = Alignment.Center) {
                    Image(painter = painterResource(id = R.drawable.logo_probasket), contentDescription = "Logo", modifier = Modifier.fillMaxSize())
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text("ProBasketAcademy", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            val isPreview = LocalInspectionMode.current
            val photoUrl = if (!isPreview) FirebaseAuth.getInstance().currentUser?.photoUrl?.toString() else null

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .clickable { onProfileClick() },
                contentAlignment = Alignment.Center
            ) {
                if (!photoUrl.isNullOrEmpty()) {
                    AsyncImage(model = photoUrl, contentDescription = "Perfil", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Icon(Icons.Default.Person, contentDescription = "Perfil", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoriaSelector(state: AsistenciasState, onEvent: (AsistenciasEvent) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text("Categoría", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextMuted)
        Spacer(modifier = Modifier.height(8.dp))
        var categoriaExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = categoriaExpanded,
            onExpandedChange = { categoriaExpanded = !categoriaExpanded }
        ) {
            OutlinedTextField(
                value = state.categoriaSeleccionadaNombre,
                onValueChange = {}, readOnly = true, label = { Text("Selecciona la categoría") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaExpanded) },
                modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(12.dp)
            )
            ExposedDropdownMenu(
                expanded = categoriaExpanded,
                onDismissRequest = { categoriaExpanded = false }
            ) {
                if (state.categorias.isEmpty()) {
                    DropdownMenuItem(text = { Text("No hay categorías") }, onClick = {}, enabled = false)
                } else {
                    state.categorias.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat.nombre) },
                            onClick = {
                                onEvent(AsistenciasEvent.OnCategoriaSelected(cat.id, cat.nombre))
                                categoriaExpanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MensajeCategoriaVacia() {
    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
        Text("Selecciona una categoría para tomar el pase de lista.", color = TextMuted, textAlign = TextAlign.Center)
    }
}

@Composable
private fun CalendarioSection(
    calendarState: CalendarState,
    visibleMonth: YearMonth,
    state: AsistenciasState,
    onEvent: (AsistenciasEvent) -> Unit,
    coroutineScope: CoroutineScope
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)) {
            val monthName = visibleMonth.month.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es"))
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "$monthName ${visibleMonth.year}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
                Row {
                    IconButton(onClick = { coroutineScope.launch { calendarState.animateScrollToMonth(visibleMonth.minusMonths(1)) } }) {
                        Icon(Icons.Default.ChevronLeft, contentDescription = "Mes Anterior", tint = TextDark)
                    }
                    IconButton(onClick = { coroutineScope.launch { calendarState.animateScrollToMonth(visibleMonth.plusMonths(1)) } }) {
                        Icon(Icons.Default.ChevronRight, contentDescription = "Mes Siguiente", tint = TextDark)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            RowDiasDeSemana()
            Spacer(modifier = Modifier.height(8.dp))

            HorizontalCalendar(
                state = calendarState,
                dayContent = { day ->
                    DayCell(
                        day = day,
                        isSelected = state.selectedDate == day.date,
                        onClick = { onEvent(AsistenciasEvent.OnDateSelected(it.date)) }
                    )
                }
            )
        }
    }
}

@Composable
private fun RowDiasDeSemana() {
    val daysOfWeek = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
    Row(modifier = Modifier.fillMaxWidth()) {
        daysOfWeek.forEach { day ->
            Text(text = day, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        }
    }
}
private fun LazyListScope.construirSeccionJugadores(
    state: AsistenciasState,
    onEvent: (AsistenciasEvent) -> Unit
) {
    item {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text("Pase de Lista", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
            Spacer(modifier = Modifier.height(4.dp))
            if (state.isEditable) {
                Text("Toca un jugador para marcar o quitar su asistencia de hoy.", fontSize = 13.sp, color = SuccessGreen, fontWeight = FontWeight.Medium)
            } else {
                Text("Modo lectura: Solo puedes ver la asistencia de este día.", fontSize = 13.sp, color = TextMuted)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }

    if (state.isLoading) {
        item { Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = HeaderOrange) } }
        return
    }

    val presentes = state.jugadores.filter { state.asistencias[it.jugadorId] == true }
    val ausentes = state.jugadores.filter { state.asistencias[it.jugadorId] != true }

    if (ausentes.isNotEmpty()) {
        item { TituloLista("Por Confirmar / Ausentes (${ausentes.size})", BadgeUrgentText) }
        item {
            CardJugadoresLista(
                jugadores = ausentes,
                isChecked = false,
                isEditable = state.isEditable,
                onItemClick = { onEvent(AsistenciasEvent.OnJugadorToggled(it.jugadorId)) }
            )
        }
    }

    if (presentes.isNotEmpty()) {
        item { TituloLista("Presentes (${presentes.size})", SuccessGreen) }
        item {
            CardJugadoresLista(
                jugadores = presentes,
                isChecked = true,
                isEditable = state.isEditable,
                onItemClick = { onEvent(AsistenciasEvent.OnSolicitarQuitarAsistencia(it)) }
            )
        }
    }

    if (state.jugadores.isEmpty()) {
        item {
            Text("No hay jugadores activos en esta categoría.", color = TextMuted, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(32.dp))
        }
    }
}

@Composable
private fun TituloLista(titulo: String, color: Color) {
    Text(
        text = titulo,
        color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun CardJugadoresLista(
    jugadores: List<Jugador>,
    isChecked: Boolean,
    isEditable: Boolean,
    onItemClick: (Jugador) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column {
            jugadores.forEachIndexed { index, jugador ->
                JugadorAsistenciaRow(
                    jugador = jugador,
                    isChecked = isChecked,
                    isEditable = isEditable,
                    onClick = { onItemClick(jugador) }
                )
                if (index < jugadores.size - 1) HorizontalDivider(color = DividerColor)
            }
        }
    }
}

@Composable
fun DayCell(day: CalendarDay, isSelected: Boolean, onClick: (CalendarDay) -> Unit) {
    val isCurrentMonth = day.position == DayPosition.MonthDate
    val textColor = when {
        isSelected -> Color.White
        !isCurrentMonth -> TextMuted.copy(alpha = 0.3f)
        else -> TextDark
    }
    val bgColor = if (isSelected) HeaderOrange else Color.Transparent

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(CircleShape)
            .background(bgColor)
            .clickable(enabled = isCurrentMonth, onClick = { onClick(day) }),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.date.dayOfMonth.toString(),
            color = textColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun JugadorAsistenciaRow(
    jugador: Jugador,
    isChecked: Boolean,
    isEditable: Boolean,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable(enabled = isEditable) { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(40.dp).clip(CircleShape).background(BorderColor),
            contentAlignment = Alignment.Center
        ) {
            if (!jugador.fotoUri.isNullOrEmpty()) {
                AsyncImage(
                    model = ImageRequest.Builder(context).data(jugador.fotoUri).crossfade(true).build(),
                    contentDescription = "Foto", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop
                )
            } else {
                Text(jugador.nombre.take(1).uppercase(), fontWeight = FontWeight.Bold, color = TextDark, fontSize = 16.sp)
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(jugador.nombre, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark, modifier = Modifier.weight(1f))

        Checkbox(
            checked = isChecked,
            onCheckedChange = { if(isEditable) onClick() },
            enabled = isEditable,
            colors = CheckboxDefaults.colors(checkedColor = SuccessGreen, checkmarkColor = Color.White)
        )
    }
}
@Preview(showBackground = true)
@Composable
fun AsistenciasScreenPreview() {
    ProBasketAcademyTheme {
        AsistenciasContent(
            state = AsistenciasState(
                jugadores = listOf(
                    Jugador(nombre = "William Rodriguez", categoriaNombre = "U-20"),
                    Jugador(nombre = "Juan Perez", categoriaNombre = "U-20")
                )
            ),
            onEvent = {},
            onProfileClick = {}
        )
    }
}