package com.probasketacademy.presentacion.eventos

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.probasketacademy.R
import com.probasketacademy.domain.model.Evento
import com.probasketacademy.ui.theme.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventosScreen(
    viewModel: EventosViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    // Obtenemos la fecha actual para bloquear días pasados
    val today = remember { LocalDate.now() }

    // --- CONFIGURACIÓN DEL CALENDARIO KIZITONWOSE ---
    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(24) } // Permitimos navegar 2 años atrás
    val endMonth = remember { currentMonth.plusMonths(24) }    // y 2 años adelante
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }

    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek
    )

    // --- DIÁLOGO PARA CREAR EVENTO ---
    if (state.showAddDialog) {
        var titulo by remember { mutableStateOf("") }
        var tipo by remember { mutableStateOf("Entrenamiento") }
        var horaStr by remember { mutableStateOf("16:00") }
        var duracionStr by remember { mutableStateOf("1.5") }
        var lugar by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { viewModel.onEvent(EventosEvent.OnToggleAddDialog) },
            title = { Text("Nuevo Evento", fontWeight = FontWeight.Bold, color = TextDark) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = titulo, onValueChange = { titulo = it }, label = { Text("Concepto") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = tipo, onValueChange = { tipo = it }, label = { Text("Tipo (Partido, Entrenamiento)") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(value = horaStr, onValueChange = { horaStr = it }, label = { Text("Hora (HH:mm)") }, singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                        OutlinedTextField(value = duracionStr, onValueChange = { duracionStr = it }, label = { Text("Duración (h)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal), singleLine = true, modifier = Modifier.weight(1f), shape = RoundedCornerShape(12.dp))
                    }
                    OutlinedTextField(value = lugar, onValueChange = { lugar = it }, label = { Text("Lugar") }, singleLine = true, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        try {
                            val time = LocalTime.parse(horaStr)
                            val dur = duracionStr.toFloatOrNull() ?: 1f
                            viewModel.onEvent(EventosEvent.OnGuardarEvento(titulo, tipo, time, dur, lugar))
                        } catch (e: Exception) { /* Podrías agregar un error de UI aquí si el formato falla */ }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = HeaderOrange)
                ) { Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.onEvent(EventosEvent.OnToggleAddDialog) }) { Text("Cancelar", color = TextMuted) }
            },
            containerColor = CardBackground
        )
    }

    Scaffold(
        floatingActionButton = {
            // LÓGICA DE BLOQUEO: Solo mostramos el botón (+) si el día seleccionado NO es anterior a hoy
            if (!state.selectedDate.isBefore(today)) {
                FloatingActionButton(
                    onClick = { viewModel.onEvent(EventosEvent.OnToggleAddDialog) },
                    containerColor = HeaderOrange,
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar Evento", modifier = Modifier.size(28.dp))
                }
            }
        },
        containerColor = LightBackground
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            // --- ENCABEZADO ESTÁNDAR ---
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

            // --- CALENDARIO KIZITONWOSE ---
            Card(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackground),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)) {
                    // Cabecera del Mes Visible
                    val visibleMonth = calendarState.firstVisibleMonth.yearMonth
                    val monthName = visibleMonth.month.getDisplayName(TextStyle.FULL, Locale("es", "ES")).replaceFirstChar { it.uppercase() }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "$monthName ${visibleMonth.year}", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, color = TextDark)
                        Row {
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    calendarState.animateScrollToMonth(visibleMonth.minusMonths(1))
                                }
                            }) { Icon(Icons.Default.ChevronLeft, contentDescription = "Mes Anterior", tint = TextDark) }

                            IconButton(onClick = {
                                coroutineScope.launch {
                                    calendarState.animateScrollToMonth(visibleMonth.plusMonths(1))
                                }
                            }) { Icon(Icons.Default.ChevronRight, contentDescription = "Mes Siguiente", tint = TextDark) }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Días de la Semana
                    val daysOfWeek = listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")
                    Row(modifier = Modifier.fillMaxWidth()) {
                        for (day in daysOfWeek) {
                            Text(
                                text = day,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextMuted,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Rejilla de días
                    HorizontalCalendar(
                        state = calendarState,
                        dayContent = { day ->
                            DayCell(
                                day = day,
                                isSelected = state.selectedDate == day.date,
                                isPast = day.date.isBefore(today),
                                onClick = {
                                    viewModel.onEvent(EventosEvent.OnDateSelected(it.date))
                                }
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // --- TÍTULO DE EVENTOS DEL DÍA ---
            val dayName = state.selectedDate.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("es", "ES")).replaceFirstChar { it.uppercase() }
            val dayNum = state.selectedDate.dayOfMonth
            val monthNameTitle = state.selectedDate.month.getDisplayName(TextStyle.SHORT, Locale("es", "ES")).replaceFirstChar { it.uppercase() }

            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (state.selectedDate == today) "Hoy, $dayNum $monthNameTitle" else "$dayName, $dayNum $monthNameTitle",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextDark
                )
                Box(
                    modifier = Modifier.background(IndicatorColor, RoundedCornerShape(12.dp)).padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text("${state.eventosDelDia.size} Eventos", color = HeaderOrange, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- LISTA DE EVENTOS ---
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator(color = HeaderOrange) }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(state.eventosDelDia) { evento ->
                        EventoItemRow(evento = evento)
                    }
                    if (state.eventosDelDia.isEmpty()) {
                        item {
                            val mensajeVacio = if (state.selectedDate.isBefore(today)) "No hubo eventos programados este día." else "Día libre, no hay eventos programados."
                            Text(mensajeVacio, color = TextMuted, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(32.dp))
                        }
                    }
                }
            }
        }
    }
}

// --- COMPONENTE PARA LA CELDA DEL DÍA ---
@Composable
fun DayCell(day: CalendarDay, isSelected: Boolean, isPast: Boolean, onClick: (CalendarDay) -> Unit) {
    val isCurrentMonth = day.position == DayPosition.MonthDate

    // Lógica de colores según el estado del día
    val textColor = when {
        isSelected -> Color.White
        !isCurrentMonth -> TextMuted.copy(alpha = 0.3f)
        isPast -> TextMuted // Fechas pasadas en color gris apagado
        else -> TextDark
    }
    val bgColor = if (isSelected) HeaderOrange else Color.Transparent

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(4.dp)
            .clip(CircleShape)
            .background(bgColor)
            .clickable(
                enabled = isCurrentMonth, // Solo dejar hacer click en días del mes activo
                onClick = { onClick(day) }
            ),
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

// --- COMPONENTE PARA EL EVENTO (Igual a tu diseño) ---
@Composable
fun EventoItemRow(evento: Evento) {
    // Calculamos colores e íconos en base al tipo (Entrenamiento, Partido, Pago, Reunión)
    val (lineColor, iconBg, iconTint, iconVector) = when (evento.tipo.lowercase()) {
        "partido" -> listOf(BlueIcon, Color(0xFFEBF3FF), BlueIcon, Icons.Default.Flag)
        "pago", "cuota" -> listOf(GreenTrend, Color(0xFFE8F5E9), GreenTrend, Icons.Default.Payments)
        "reunión", "reunion" -> listOf(TextMuted, DividerColor, TextMuted, Icons.Default.ChatBubbleOutline)
        else -> listOf(HeaderOrange, IndicatorColor, HeaderOrange, Icons.Default.SportsBasketball)
    }

    val time = java.time.Instant.ofEpochMilli(evento.fechaHoraEpocaMs).atZone(ZoneId.systemDefault()).toLocalTime()
    val formatter = DateTimeFormatter.ofPattern("HH:mm")

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(50.dp)) {
                Text(text = time.format(formatter), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = TextDark)
                Text(text = "${evento.duracionHoras}h", fontSize = 11.sp, color = TextMuted)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Box(modifier = Modifier.width(3.dp).height(40.dp).background(lineColor as Color, RoundedCornerShape(50)))
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = evento.titulo, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = TextMuted, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = evento.lugar, fontSize = 12.sp, color = TextMuted)
                }
            }
            Box(
                modifier = Modifier.size(40.dp).background(iconBg as Color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(iconVector as ImageVector, contentDescription = null, tint = iconTint as Color, modifier = Modifier.size(20.dp))
            }
        }
    }
}