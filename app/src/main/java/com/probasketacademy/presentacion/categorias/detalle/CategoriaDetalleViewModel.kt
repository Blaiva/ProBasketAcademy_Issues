package com.probasketacademy.presentacion.categorias.detalle

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.probasketacademy.domain.model.Categoria
import com.probasketacademy.domain.model.Jugador
import com.probasketacademy.domain.repository.CategoriaRepository
import com.probasketacademy.domain.repository.JugadorRepository
import com.probasketacademy.domain.usecase.categoria.EliminarCategoriaUseCase
import com.probasketacademy.domain.usecase.categoria.GuardarCategoriaUseCase
import com.probasketacademy.domain.usecase.categoria.ObtenerCategoriaPorIdUseCase
import com.probasketacademy.domain.usecase.categoria.validarNombreCategoria
import com.probasketacademy.domain.usecase.jugadores.AsignarJugadoresACategoriaUseCase
import com.probasketacademy.domain.usecase.jugadores.ObtenerJugadoresPorCategoriaUseCase
import com.probasketacademy.domain.usecase.jugadores.ObtenerJugadoresSinCategoriaUseCase
import com.probasketacademy.domain.usecase.jugadores.RemoverJugadorDeCategoriaUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.map

@HiltViewModel
class CategoriaDetalleViewModel @Inject constructor(
    private val categoriaRepository: CategoriaRepository,
    private val obtenerCategoriaPorIdUseCase: ObtenerCategoriaPorIdUseCase,
    private val guardarCategoriaUseCase: GuardarCategoriaUseCase,
    private val eliminarCategoriaUseCase: EliminarCategoriaUseCase,
    private val obtenerJugadoresPorCategoriaUseCase: ObtenerJugadoresPorCategoriaUseCase,
    private val obtenerJugadoresSinCategoriaUseCase: ObtenerJugadoresSinCategoriaUseCase,
    private val asignarJugadoresACategoriaUseCase: AsignarJugadoresACategoriaUseCase,
    private val removerJugadorDeCategoriaUseCase: RemoverJugadorDeCategoriaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriaDetalleState())
    val uiState: StateFlow<CategoriaDetalleState> = _uiState.asStateFlow()

    fun onEvent(event: CategoriaDetalleEvent) {
        when (event) {
            is CategoriaDetalleEvent.OnCargarDetalle -> cargarDetalle(event.categoriaId)
            is CategoriaDetalleEvent.OnNombreCategoriaChanged -> {
                _uiState.update { it.copy(nombreCategoria = event.nombre, nombreError = null) }
            }
            is CategoriaDetalleEvent.OnGuardarNombreCategoria -> guardarNombreCategoria()
            is CategoriaDetalleEvent.OnShowSaveSuccessDialogChanged -> {
                _uiState.update { it.copy(showSaveSuccessDialog = event.show) }
            }
            is CategoriaDetalleEvent.OnShowDeleteConfirmDialogChanged -> {
                _uiState.update { it.copy(showDeleteConfirmDialog = event.show) }
            }
            is CategoriaDetalleEvent.OnEliminarCategoria -> eliminarCategoria()
            is CategoriaDetalleEvent.OnShowDeleteSuccessDialogChanged -> {
                _uiState.update { it.copy(showDeleteSuccessDialog = event.show) }
            }
            is CategoriaDetalleEvent.OnShowAddJugadoresDialogChanged -> {
                _uiState.update {
                    it.copy(
                        showAddJugadoresDialog = event.show,
                        selectedJugadoresIds = emptySet()
                    )
                }
            }
            is CategoriaDetalleEvent.OnJugadorSelectionToggled -> {
                _uiState.update { state ->
                    val currentSet = state.selectedJugadoresIds.toMutableSet()
                    if (currentSet.contains(event.jugadorId)) {
                        currentSet.remove(event.jugadorId)
                    } else {
                        currentSet.add(event.jugadorId)
                    }
                    state.copy(selectedJugadoresIds = currentSet)
                }
            }
            is CategoriaDetalleEvent.OnAsignarJugadoresSeleccionados -> asignarJugadoresSeleccionados()
            is CategoriaDetalleEvent.OnRemoverJugador -> removerJugador(event.jugador)
        }
    }

    private fun cargarDetalle(categoriaId: Long) {
        _uiState.update { it.copy(categoriaId = categoriaId, isLoading = true) }

        viewModelScope.launch {
            obtenerCategoriaPorIdUseCase(categoriaId).collectLatest { cat ->
                cat?.let { categoria ->
                    _uiState.update { it.copy(nombreCategoria = categoria.nombre) }
                }
            }
        }

        viewModelScope.launch {
            obtenerJugadoresPorCategoriaUseCase(categoriaId).collectLatest { list ->
                _uiState.update { it.copy(jugadoresAsignados = list, isLoading = false) }
            }
        }

        viewModelScope.launch {
            obtenerJugadoresSinCategoriaUseCase().collectLatest { list ->
                _uiState.update { it.copy(jugadoresSinCategoria = list) }
            }
        }
    }

    private fun guardarNombreCategoria() {
        viewModelScope.launch {
            val nombre = uiState.value.nombreCategoria
            val nombresExistentes = categoriaRepository.obtenerCategoriasConConteo().first().map { it.nombre }

            val nombreValidation = validarNombreCategoria(nombre, nombresExistentes)

            if (!nombreValidation.isValid) {
                _uiState.update { it.copy(nombreError = nombreValidation.error) }
                return@launch
            }

            _uiState.update { it.copy(isSavingNombre = true) }
            val categoria = Categoria(
                id = uiState.value.categoriaId,
                nombre = nombre
            )

            val result = guardarCategoriaUseCase(categoria)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isSavingNombre = false,
                        showSaveSuccessDialog = true,
                        errorMessage = null
                    )
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSavingNombre = false,
                        errorMessage = error.message
                    )
                }
            }
        }
    }

    private fun eliminarCategoria() {
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, showDeleteConfirmDialog = false) }
            eliminarCategoriaUseCase(uiState.value.categoriaId)
            _uiState.update {
                it.copy(
                    isDeleting = false,
                    showDeleteSuccessDialog = true
                )
            }
        }
    }

    private fun asignarJugadoresSeleccionados() {
        val selectedIds = uiState.value.selectedJugadoresIds
        if (selectedIds.isEmpty()) return

        val jugadoresAAsignar = uiState.value.jugadoresSinCategoria.filter { selectedIds.contains(it.jugadorId) }
        val categoriaId = uiState.value.categoriaId
        val categoriaNombre = uiState.value.nombreCategoria

        viewModelScope.launch {
            _uiState.update { it.copy(isAssigning = true) }
            asignarJugadoresACategoriaUseCase(jugadoresAAsignar, categoriaId, categoriaNombre)
            _uiState.update {
                it.copy(
                    isAssigning = false,
                    showAddJugadoresDialog = false,
                    selectedJugadoresIds = emptySet()
                )
            }
        }
    }

    private fun removerJugador(jugador: Jugador) {
        viewModelScope.launch {
            removerJugadorDeCategoriaUseCase(jugador)
        }
    }
}