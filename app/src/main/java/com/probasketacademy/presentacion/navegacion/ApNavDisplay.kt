package com.probasketacademy.presentacion.navegacion

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.FactCheck
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.probasketacademy.presentacion.iniciodesesion.AuthScreen
import com.probasketacademy.presentacion.home.HomeScreen
import com.probasketacademy.presentacion.jugadores.edit.JugadorEditScreen
import com.probasketacademy.presentacion.jugadores.list.JugadoresListScreen

// Colores de tu diseño
private val PrimaryOrange = Color(0xFFE5634D)
private val TextMuted = Color(0xFF94A3B8)
private val IndicatorColor = Color(0xFFFFF7ED)

// Modelo para los items del menú
data class BottomNavItem(
    val title: String,
    val route: Screen,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun ApNavDisplay(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Definimos en qué pantallas SÍ queremos ver el menú inferior
    val showBottomBar = currentDestination?.route?.let { route ->
        route.contains(Screen.Home::class.simpleName ?: "") ||
                route.contains(Screen.Jugadores::class.simpleName ?: "") ||
                route.contains(Screen.Categorias::class.simpleName ?: "") ||
                route.contains(Screen.Eventos::class.simpleName ?: "") ||
                route.contains(Screen.Asistencias::class.simpleName ?: "") ||
                route.contains(Screen.Pagos::class.simpleName ?: "")
    } ?: false

    Scaffold(
        modifier = modifier,
        bottomBar = {
            if (showBottomBar) {
                ProBasketBottomBar(navController = navController, currentDestination = currentDestination)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Auth,
            modifier = Modifier.padding(innerPadding)
        ) {
            // 1. Pantalla de Login
            composable<Screen.Auth> {
                AuthScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Home) {
                            popUpTo(Screen.Auth) { inclusive = true }
                        }
                    }
                )
            }

            // 2. Pantalla de Inicio (Dashboard)
            composable<Screen.Home> {
                HomeScreen(
                    onNavigateTo = { route -> navController.navigate(route) },
                    onLogout = {
                        navController.navigate(Screen.Auth) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            // 3. Directorio de Jugadores
            composable<Screen.Jugadores> {
                JugadoresListScreen(
                    onNavigateToDetail = { jugadorId ->
                        navController.navigate(Screen.JugadorEdit(jugadorId))
                    },
                    onAddJugador = {
                        // Aquí navegarías a una pantalla de crear jugador vacío
                    }
                )
            }

            // 4. Edición / Detalle de Jugador (Recibe ID)
            composable<Screen.JugadorEdit> { backStackEntry ->
                // Extraemos el ID de forma segura con Type-Safe Navigation
                val args = backStackEntry.toRoute<Screen.JugadorEdit>()

                JugadorEditScreen(
                    onNavigateBack = { navController.popBackStack() }
                    // El ViewModel obtendrá el args.jugadorId a través del SavedStateHandle automáticamente
                )
            }

            // --- Placeholders para las demás pantallas ---
            composable<Screen.Categorias> { Text("Categorías Próximamente", modifier = Modifier.padding(16.dp)) }
            composable<Screen.Eventos> { Text("Calendario Próximamente", modifier = Modifier.padding(16.dp)) }
            composable<Screen.Asistencias> { Text("Asistencia Próximamente", modifier = Modifier.padding(16.dp)) }
            composable<Screen.Pagos> { Text("Finanzas Próximamente", modifier = Modifier.padding(16.dp)) }
        }
    }
}

@Composable
private fun ProBasketBottomBar(
    navController: NavHostController,
    currentDestination: androidx.navigation.NavDestination?
) {
    val items = listOf(
        BottomNavItem("Inicio", Screen.Home, Icons.Filled.Home, Icons.Outlined.Home),
        BottomNavItem("Jugadores", Screen.Jugadores, Icons.Filled.Person, Icons.Outlined.Person),
        BottomNavItem("Categorías", Screen.Categorias, Icons.Filled.Groups, Icons.Outlined.Groups),
        BottomNavItem("Calendario", Screen.Eventos, Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
        BottomNavItem("Asistencia", Screen.Asistencias, Icons.Filled.FactCheck, Icons.Outlined.FactCheck),
        BottomNavItem("Finanzas", Screen.Pagos, Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet)
    )

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp // Sombra suave en la parte superior
    ) {
        items.forEach { item ->
            // Comprobamos si la ruta actual coincide con la del ítem
            val isSelected = currentDestination?.hierarchy?.any {
                it.route?.contains(item.route::class.simpleName ?: "") == true
            } == true

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        // Evita que se acumulen múltiples copias de la misma pantalla
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.title
                    )
                },
                label = {
                    Text(
                        text = item.title,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryOrange,
                    selectedTextColor = PrimaryOrange,
                    indicatorColor = IndicatorColor, // El fondo sutil cuando está seleccionado
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                )
            )
        }
    }
}