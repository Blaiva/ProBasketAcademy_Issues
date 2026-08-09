package com.probasketacademy.presentacion.categorias.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.probasketacademy.domain.model.Categoria
import com.probasketacademy.domain.repository.CategoriaRepository
import com.probasketacademy.domain.usecase.categoria.GuardarCategoriaUseCase
import com.probasketacademy.domain.usecase.categoria.validarNombreCategoria
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoriasListViewModel @Inject constructor(
    private val categoriaRepository: CategoriaRepository,
    private val guardarCategoriaUseCase: GuardarCategoriaUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoriasListState())
    val uiState: StateFlow<CategoriasListState> = _uiState.asStateFlow()

    init {
        cargarCategorias()
    }

    private fun cargarCategorias() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            categoriaRepository.obtenerCategoriasConConteo().collectLatest { list -> _uiState.update { it.copy(isLoading = false, categorias = list, message = null) } }
        }
    }

    fun onEvent(event: CategoriasListEvent) {
        when (event) {
            is CategoriasListEvent.OnGuardarCategoria -> onSave()
        }
    }

    private fun onSave(){
        viewModelScope.launch {
            val nombre = uiState.value.nombre
            val nombresExistentes = categoriaRepository.obtenerCategoriasConConteo().first().map { it.nombre }

            val nombreValidation = validarNombreCategoria(nombre, nombresExistentes)

            if(!nombreValidation.isValid){
                _uiState.update { it.copy(
                    nombreError = nombreValidation.error
                ) }
                return@launch
            }

            _uiState.update { it.copy(isSaving = true) }
            val categoria = Categoria(
                nombre = nombre
            )

            val result = guardarCategoriaUseCase(categoria)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        isSaving = false
                    )
                }
            }.onFailure {
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }
}