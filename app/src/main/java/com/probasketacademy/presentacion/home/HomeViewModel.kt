package com.probasketacademy.presentacion.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.probasketacademy.domain.model.Pago
import com.probasketacademy.domain.repository.PagoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val pagoRepository: PagoRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeState())
    val uiState: StateFlow<HomeState> = _uiState.asStateFlow()

    init {
        cargarCobrosPendientes()
    }

    private fun cargarCobrosPendientes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            pagoRepository.obtenerCobrosPendientes()
                .catch { exception ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = exception.message)
                    }
                }
                .collect { pagos ->
                    // Si la base de datos está vacía, mostramos datos de prueba para igualar tu diseño.
                    // Una vez tengas datos reales, puedes quitar este "if" y dejar solo la asignación de "pagos".
                    val pagosAMostrar = if (pagos.isEmpty()) getDummyPagos() else pagos

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            cobrosPendientes = pagosAMostrar
                        )
                    }
                }
        }
    }

    // Datos de prueba para que se vea exactamente como en tu diseño de Figma
    private fun getDummyPagos(): List<Pago> {
        return listOf(
            Pago(id = 1, jugadorNombre = "Mateo Pérez", concepto = "Cuota Julio", monto = 45.0, fecha = "Hace 5 días", estado = "PENDIENTE", jugadorId = 1),
            Pago(id = 2, jugadorNombre = "Laura Gómez", concepto = "Uniforme", monto = 80.0, fecha = "Hace 2 días", estado = "PENDIENTE", jugadorId = 2),
            Pago(id = 3, jugadorNombre = "Diego Ruiz", concepto = "Torneo Verano", monto = 25.0, fecha = "Hoy", estado = "PENDIENTE", jugadorId = 3)
        )
    }
}