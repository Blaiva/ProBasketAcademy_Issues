package com.probasketacademy.presentacion.navegacion

import androidx.compose.foundation.layout.PaddingValues
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay

// --- IMPORTACIONES DE TODAS TUS PANTALLAS ---
import com.probasketacademy.presentacion.auth.AuthScreen
import com.probasketacademy.presentacion.home.HomeScreen
import com.probasketacademy.presentacion.jugadores.list.JugadoresListScreen
import com.probasketacademy.presentacion.jugadores.edit.JugadorEditScreen
import com.probasketacademy.presentacion.categorias.list.CategoriasListScreen
import com.probasketacademy.presentacion.categorias.asignar.CategoriaAsignarScreen
import com.probasketacademy.presentacion.categorias.detalle.CategoriaDetalleScreen
import com.probasketacademy.presentacion.asistencias.AsistenciasScreen
import com.probasketacademy.presentacion.eventos.EventosScreen
import com.probasketacademy.presentacion.finanzas.PagosScreen

// --- IMPORTACIONES DE TU TEMA ---
import com.probasketacademy.ui.theme.IndicatorColor
import com.probasketacademy.ui.theme.PrimaryOrange
import com.probasketacademy.ui.theme.TextMuted

data class BottomNavItem(
    val title: String,
    val route: Screen,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@Composable
fun AppNavDisplay(
    backStack: NavBackStack<NavKey>,
    innerPadding: PaddingValues
) {
    val currentScreen = backStack.lastOrNull()

    val showBottomBar = currentScreen is Screen.Home ||
            currentScreen is Screen.Jugadores ||
            currentScreen is Screen.Categorias ||
            currentScreen is Screen.Eventos ||
            currentScreen is Screen.Asistencias ||
            currentScreen is Screen.Pagos

    Scaffold(
        modifier = Modifier.padding(innerPadding),
        bottomBar = {
            if (showBottomBar) {
                ProBasketBottomBar(backStack = backStack, currentScreen = currentScreen)
            }
        }
    ) { innerPadding ->
        NavDisplay(
            backStack = backStack,
            modifier = Modifier.padding(innerPadding),
            entryProvider = entryProvider {
                entry<Screen.Auth> {
                    AuthScreen(
                        onLoginSuccess = {
                            backStack.clear()
                            backStack.add(Screen.Home)
                        }
                    )
                }

                entry<Screen.Home> {
                    HomeScreen(
                        onNavigateTo = { route -> backStack.add(route) },
                        onLogout = {
                            backStack.clear()
                            backStack.add(Screen.Auth)
                        }
                    )
                }

                entry<Screen.Jugadores> {
                    JugadoresListScreen(
                        onNavigateToDetail = { jugadorId ->
                            backStack.add(Screen.JugadorEdit(jugadorId))
                        },
                        onAddJugador = {
                            backStack.add(Screen.JugadorEdit(0L))
                        }
                    )
                }

                entry<Screen.JugadorEdit> { key ->
                    JugadorEditScreen(
                        jugadorId = key.jugadorId,
                        onNavigateBack = { if(backStack.isNotEmpty()) backStack.removeAt(backStack.size - 1) }
                    )
                }

                entry<Screen.Categorias> {
                    CategoriasListScreen(
                        onNavigateToAsignarJugador = { categoriaId ->
                            backStack.add(Screen.CategoriaAsignar(categoriaId))
                        },
                        onNavigateToVerEditar = { categoriaId ->
                            backStack.add(Screen.CategoriaDetalle(categoriaId))
                        },
                        onAddCategoria = {
                        }
                    )
                }

                entry<Screen.CategoriaAsignar> {
                    CategoriaAsignarScreen(
                        onNavigateBack = { if(backStack.isNotEmpty()) backStack.removeAt(backStack.size - 1) }
                    )
                }

                // 5.2 Ver Detalles de Categoría
                entry<Screen.CategoriaDetalle> { key ->
                    CategoriaDetalleScreen(
                        categoriaId = key.categoriaId,
                        onNavigateBack = { if(backStack.isNotEmpty()) backStack.removeAt(backStack.size - 1) }
                    )
                }

                // 6. PASE DE LISTA (ASISTENCIAS)
                entry<Screen.Asistencias> {
                    AsistenciasScreen(
                        onNavigateBack = {
                            if(backStack.isNotEmpty()) backStack.removeAt(backStack.size - 1)
                        }
                    )
                }

                // 7. CALENDARIO Y EVENTOS
                entry<Screen.Eventos> {
                    EventosScreen()
                }

                // 8. FINANZAS / PAGOS
                entry<Screen.Pagos> {
                    PagosScreen()
                }
            }
        )
    }
}

@Composable
private fun ProBasketBottomBar(
    backStack: MutableList<NavKey>,
    currentScreen: NavKey?
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
        tonalElevation = 8.dp
    ) {
        items.forEach { item ->
            val isSelected = currentScreen?.javaClass == item.route.javaClass

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentScreen != item.route) {
                        val existingIndex = backStack.indexOf(item.route)
                        if (existingIndex != -1) {
                            while (backStack.lastIndex > existingIndex) {
                                backStack.removeAt(backStack.lastIndex)
                            }
                        } else {
                            backStack.add(item.route)
                        }
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
                    indicatorColor = IndicatorColor,
                    unselectedIconColor = TextMuted,
                    unselectedTextColor = TextMuted
                )
            )
        }
    }
}