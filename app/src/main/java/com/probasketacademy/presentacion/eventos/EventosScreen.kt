package com.probasketacademy.presentacion.eventos

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil3.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.kizitonwose.calendar.compose.CalendarState
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.probasketacademy.R
import com.probasketacademy.domain.model.Evento
import com.probasketacademy.presentacion.perfil.ProfileDialog
import com.probasketacademy.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun EventosScreen(
    onLogout: () -> Unit,
    viewModel: EventosViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showProfileDialog by remember { mutableStateOf(false) }

    if (showProfileDialog) {
        ProfileDialog(
            user = FirebaseAuth.getInstance().currentUser,
            onDismiss = { showProfileDialog = false },
            onLogout = { showProfileDialog = false; onLogout() }
        )
    }

    ManejoDeDialogosEventos(state, viewModel::onEvent)

    EventosContent(
        state = state,
        onEvent = viewModel::onEvent,
        onProfileClick = { showProfileDialog = true }
    )
}

@Composable
fun EventosContent(
    state: EventosState,
    onEvent: (EventosEvent) -> Unit,
    onProfileClick: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val today = remember { LocalDate.now() }
    val currentMonth = remember { YearMonth.now() }
    val calendarState = rememberCalendarState(
        startMonth = currentMonth.minusMonths(24),
        endMonth = currentMonth.plusMonths(24),
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = remember { firstDayOfWeekFromLocale() }
    )
    val visibleMonth = calendarState.firstVisibleMonth.yearMonth

    LaunchedEffect(visibleMonth) {
        onEvent(EventosEvent.OnVisibleMonthChanged(visibleMonth))
    }

    Scaffold(
        floatingActionButton = { BotonAgregarEvento(state.selectedDate, today, onEvent) },
        containerColor = LightBackground
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(bottom = padding.calculateBottomPadding()),
            contentPadding = PaddingValues(bottom = 88.dp)
        ) {
            item {
                EventosHeader(onProfileClick)
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                CalendarioEventos(calendarState, visibleMonth, state, today, onEvent, coroutineScope)
                Spacer(modifier = Modifier.height(24.dp))
            }

            seccionListaEventos(state, today, onEvent)
        }
    }
}

@Composable
private fun ManejoDeDialogosEventos(state: EventosState, onEvent: (EventosEvent) -> Unit) {
    if (state.showAddDialog) {
        DialogoAgregarEvento(onEvent)
    }

    if (state.showEditDialog && state.eventoSeleccionado != null) {
        DialogoEditarEvento(state.eventoSeleccionado, onEvent)
    }
}

private data class FormularioEventoData(
    val titulo: String = "",
    val tipo: String = "",
    val horaStr: String = "",
    val duracionStr: String = "",
    val lugar: String = ""
)

@Composable
private fun DialogoAgregarEvento(onEvent: (EventosEvent) -> Unit) {
    var formData by remember { mutableStateOf(FormularioEventoData()) }

    AlertDialog(
        onDismissRequest = { onEvent(EventosEvent.OnToggleAddDialog) },
        title = { Text("Nuevo Evento", fontWeight = FontWeight.Bold, color = TextDark) },
        text = {
            FormularioEvento(
                data = formData,
                onDataChange = { formData = it }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    try {
                        val time = LocalTime.parse(formData.horaStr)
                        val dur = formData.duracionStr.toFloatOrNull() ?: 1f
                        onEvent(EventosEvent.OnGuardarEvento(formData.titulo, formData.tipo, time, dur, formData.lugar))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = HeaderOrange)
            ) { Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = { onEvent(EventosEvent.OnToggleAddDialog) }) { Text("Cancelar", color = TextMuted) }
        },
        containerColor = CardBackground
    )
}

@Composable
private fun DialogoEditarEvento(evento: Evento, onEvent: (EventosEvent) -> Unit) {
    var formData by remember(evento.id) {
        mutableStateOf(
            FormularioEventoData(
                titulo = evento.titulo,
                tipo = evento.tipo,
                horaStr = java.time.Instant.ofEpochMilli(evento.fechaHoraEpocaMs)
                    .atZone(ZoneId.systemDefault()).toLocalTime()
                    .format(DateTimeFormatter.ofPattern("HH:mm")),
                duracionStr = evento.duracionHoras.toString(),
                lugar = evento.lugar
            )
        )
    }

    AlertDialog(
        onDismissRequest = { onEvent(EventosEvent.OnToggleEditDialog) },
        title = { Text("Editar Evento", fontWeight = FontWeight.Bold, color = TextDark) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                FormularioEvento(
                    data = formData,
                    onDataChange = { formData = it }
                )
                TextButton(
                    onClick = { onEvent(EventosEvent.OnEliminarEvento) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Eliminar Evento", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    try {
                        val time = LocalTime.parse(formData.horaStr)
                        val dur = formData.duracionStr.toFloatOrNull() ?: 1f
                        onEvent(EventosEvent.OnActualizarEvento(formData.titulo, formData.tipo, time, dur, formData.lugar))
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = HeaderOrange)
            ) { Text("Guardar Cambios", color = Color.White, fontWeight = FontWeight.Bold) }
        },
        dismissButton = {
            TextButton(onClick = { onEvent(EventosEvent.OnToggleEditDialog) }) { Text("Cancelar", color = TextMuted) }
        },
        containerColor = CardBackground
    )
}

@Composable
private fun FormularioEvento(
    data: FormularioEventoData,
    onDataChange: (FormularioEventoData) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(value = data.titulo, onValueChange = { onDataChange(data.copy(titulo = it)) }, label = { Text("Concepto") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        OutlinedTextField(value = data.tipo, onValueChange = { onDataChange(data.copy(tipo = it)) }, label = { Text("Tipo (Partido, Pago, Reunión)") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(value = data.horaStr, onValueChange = { onDataChange(data.copy(horaStr = it)) }, label = { Text("Hora (HH:mm)") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
            OutlinedTextField(value = data.duracionStr, onValueChange = { onDataChange(data.copy(duracionStr = it)) }, label = { Text("Duración (h)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
        }
        OutlinedTextField(value = data.lugar, onValueChange = { onDataChange(data.copy(lugar = it)) }, label = { Text("Lugar o Subtítulo") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
    }
}

@Composable
private fun BotonAgregarEvento(selectedDate: LocalDate, today: LocalDate, onEvent: (EventosEvent) -> Unit) {
    if (!selectedDate.isBefore(today)) {
        FloatingActionButton(
            onClick = { onEvent(EventosEvent.OnToggleAddDialog) },
            containerColor = HeaderOrange,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Add, contentDescription = "Agregar Evento", modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
private fun EventosHeader(onProfileClick: () -> Unit) {
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

@Composable
private fun CalendarioEventos(
    calendarState: CalendarState,
    visibleMonth: YearMonth,
    state: EventosState,
    today: LocalDate,
    onEvent: (EventosEvent) -> Unit,
    coroutineScope: CoroutineScope
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)) {
            val monthName = visibleMonth.month.getDisplayName(TextStyle.FULL, Locale.forLanguageTag("es")).replaceFirstChar { it.uppercase() }
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
            DiasDeLaSemanaRow()
            Spacer(modifier = Modifier.height(8.dp))

            HorizontalCalendar(
                state = calendarState,
                dayContent = { day ->
                    DayCell(
                        day = day,
                        isSelected = state.selectedDate == day.date,
                        isPast = day.date.isBefore(today),
                        hasEvent = state.diasConEventos.contains(day.date),
                        onClick = { onEvent(EventosEvent.OnDateSelected(it.date)) }
                    )
                }
            )
        }
    }
}

@Composable
private fun DiasDeLaSemanaRow() {
    val daysOfWeek = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
    Row(modifier = Modifier.fillMaxWidth()) {
        for (day in daysOfWeek) {
            Text(text = day, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextMuted, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
        }
    }
}

private fun LazyListScope.seccionListaEventos(state: EventosState, today: LocalDate, onEvent: (EventosEvent) -> Unit) {
    item {
        val dayName = state.selectedDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("es")).replaceFirstChar { it.uppercase() }
        val dayNum = state.selectedDate.dayOfMonth
        val monthNameTitle = state.selectedDate.month.getDisplayName(TextStyle.SHORT, Locale.forLanguageTag("es")).replaceFirstChar { it.uppercase() }
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (state.selectedDate == today) "Hoy, $dayNum $monthNameTitle" else "$dayName, $dayNum $monthNameTitle",
                fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = TextDark
            )
            Box(modifier = Modifier.background(IndicatorColor, RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 6.dp)) {
                Text("${state.eventosDelDia.size} Eventos", color = HeaderOrange, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }

    if (state.isLoading) {
        item {
            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = HeaderOrange)
            }
        }
    } else if (state.eventosDelDia.isEmpty()) {
        item {
            val mensajeVacio = if (state.selectedDate.isBefore(today)) "No hubo eventos programados este día." else "Día libre, no hay eventos programados."
            Text(mensajeVacio, color = TextMuted, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(32.dp))
        }
    } else {
        items(state.eventosDelDia) { evento ->
            Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                EventoItemRow(evento = evento, onClick = { onEvent(EventosEvent.OnEventoClicked(evento)) })
            }
        }
    }
}

@Composable
fun DayCell(day: CalendarDay, isSelected: Boolean, isPast: Boolean, hasEvent: Boolean, onClick: (CalendarDay) -> Unit) {
    val isCurrentMonth = day.position == DayPosition.MonthDate
    val textColor = when {
        isSelected -> Color.White
        !isCurrentMonth -> TextMuted.copy(alpha = 0.3f)
        isPast -> TextMuted
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
        Text(text = day.date.dayOfMonth.toString(), color = textColor, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium, fontSize = 14.sp)
        if (hasEvent) {
            Box(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 4.dp).size(4.dp).background(if (isSelected) Color.White else BlueIcon, CircleShape))
        }
    }
}

@Composable
fun EventoItemRow(evento: Evento, onClick: () -> Unit) {
    val lineColor: Color
    val iconBg: Color
    val iconTint: Color
    val iconVector: ImageVector
    val subtitleIcon: ImageVector

    when (evento.tipo.lowercase()) {
        "partido" -> { lineColor = BlueIcon; iconBg = Color(0xFFEBF3FF); iconTint = BlueIcon; iconVector = Icons.Default.Flag; subtitleIcon = Icons.Default.EmojiEvents }
        "pago", "cuota" -> { lineColor = GreenTrend; iconBg = Color(0xFFE8F5E9); iconTint = GreenTrend; iconVector = Icons.Default.Payments; subtitleIcon = Icons.Default.Notifications }
        "reunión", "reunion" -> { lineColor = TextMuted; iconBg = DividerColor; iconTint = TextMuted; iconVector = Icons.Default.Chat; subtitleIcon = Icons.Default.Group }
        else -> { lineColor = HeaderOrange; iconBg = IndicatorColor; iconTint = HeaderOrange; iconVector = Icons.Default.SportsBasketball; subtitleIcon = Icons.Default.LocationOn }
    }

    val time = java.time.Instant.ofEpochMilli(evento.fechaHoraEpocaMs).atZone(ZoneId.systemDefault()).toLocalTime()
    val formatter = DateTimeFormatter.ofPattern("HH:mm")
    val durationText = if (evento.duracionHoras > 0f && evento.duracionHoras < 1f) {
        "${(evento.duracionHoras * 60).toInt()}m"
    } else if (evento.duracionHoras % 1.0f == 0f) {
        "${evento.duracionHoras.toInt()}h"
    } else {
        "${evento.duracionHoras}h"
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(55.dp)) {
                Text(text = time.format(formatter), fontWeight = FontWeight.ExtraBold, fontSize = 18.sp, color = TextDark)
                Text(text = durationText, fontSize = 12.sp, color = TextMuted)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(modifier = Modifier.width(3.dp).height(40.dp).background(lineColor, RoundedCornerShape(50)))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = evento.titulo, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(subtitleIcon, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = evento.lugar, fontSize = 13.sp, color = TextMuted)
                }
            }
            Box(modifier = Modifier.size(44.dp).background(iconBg, CircleShape), contentAlignment = Alignment.Center) {
                Icon(iconVector, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun EventosScreenPreview() {
    ProBasketAcademyTheme {
        EventosContent(
            state = EventosState(
                eventosDelDia = listOf(
                    Evento(titulo = "Entrenamiento U-20", tipo = "Entrenamiento", lugar = "Cancha Central")
                )
            ),
            onEvent = {},
            onProfileClick = {}
        )
    }
}