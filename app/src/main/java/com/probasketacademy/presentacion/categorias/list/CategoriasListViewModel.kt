package com.probasketacademy.presentacion.categorias.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.probasketacademy.domain.model.Categoria
import com.probasketacademy.domain.repository.CategoriaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriasListViewModel @Inject constructor(
    private val categoriaRepository: CategoriaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriasListState())
    val uiState: StateFlow<CategoriasListState> = _uiState.asStateFlow()

    init {
        cargarCategorias()
    }

    private fun cargarCategorias() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            categoriaRepository.obtenerCategoriasConConteo()
                .catch { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
                .collect { lista ->
                    _uiState.update { it.copy(isLoading = false, categorias = lista) }
                }
        }
    }

    fun onEvent(event: CategoriasListEvent) {
        when (event) {
            is CategoriasListEvent.OnGuardarCategoria -> {
                viewModelScope.launch {
                    // Guardamos la nueva categoría en la base de datos local
                    categoriaRepository.guardarCategoria(Categoria(nombre = event.nombre))
                    // Al guardarla, el Flow de Room (cargarCategorias) actualizará la lista automáticamente
                }
            }
            else -> {
                // Los eventos de navegación se manejan en la UI (Screen)
            }
        }
    }
}