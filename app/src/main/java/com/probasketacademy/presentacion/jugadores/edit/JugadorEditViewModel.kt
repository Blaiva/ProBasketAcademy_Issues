package com.probasketacademy.presentacion.jugadores.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.probasketacademy.domain.model.Jugador
import com.probasketacademy.domain.repository.JugadorRepository
import com.probasketacademy.domain.usecase.categoria.ObtenerCategoriasConConteoUseCase
import com.probasketacademy.domain.usecase.jugadores.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class JugadorEditViewModel @Inject constructor(
    private val obtenerJugadorPorIdUseCase: ObtenerJugadorPorIdUseCase,
    private val guardarJugadorUseCase: GuardarJugadorUseCase,
    private val eliminarJugadorUseCase: EliminarJugadorUseCase,
    private val obtenerCategoriasConConteoUseCase: ObtenerCategoriasConConteoUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(JugadorEditState())
    val uiState: StateFlow<JugadorEditState> = _uiState.asStateFlow()

    init {
        cargarCategorias()
    }

    fun onEvent(event: JugadorEditEvent) {
        when (event) {
            is JugadorEditEvent.OnNombreChanged -> _uiState.update { it.copy(nombre = event.value, nombreError = null) }
            is JugadorEditEvent.OnTelefonoChanged -> _uiState.update { it.copy(telefono = event.value, telefonoError = null) }
            is JugadorEditEvent.OnEdadChanged -> _uiState.update { it.copy(edad = event.value, edadError = null) }
            is JugadorEditEvent.OnDomicilioChanged -> _uiState.update { it.copy(domicilio = event.value, domicilioError = null) }
            is JugadorEditEvent.OnCategoriaSelected -> _uiState.update { it.copy(categoriaId = event.id, categoriaNombre = event.nombre) }
            is JugadorEditEvent.OnTallaCamisetaChanged -> _uiState.update { it.copy(tallaCamiseta = event.value, tallaCamisetaError = null) }
            is JugadorEditEvent.OnNumeroCamisetaChanged -> _uiState.update { it.copy(numeroCamiseta = event.value, numeroCamisetaError = null) }
            is JugadorEditEvent.OnEstaturaChanged -> _uiState.update { it.copy(estatura = event.value, estaturaError = null) }
            is JugadorEditEvent.OnPesoChanged -> _uiState.update { it.copy(peso = event.value, pesoError = null) }
            is JugadorEditEvent.OnTutorNombreChanged -> _uiState.update { it.copy(tutorNombre = event.value, tutorNombreError = null) }
            is JugadorEditEvent.OnTutorTelefonoChanged -> _uiState.update { it.copy(tutorTelefono = event.value, tutorTelefonoError = null) }
            is JugadorEditEvent.OnTutorVinculoChanged -> _uiState.update { it.copy(tutorVinculo = event.value, tutorVinculoError = null) }
            is JugadorEditEvent.OnTutorCorreoChanged -> _uiState.update { it.copy(tutorCorreo = event.value, tutorCorreoError = null) }
            is JugadorEditEvent.OnEstadoChanged -> _uiState.update { it.copy(estado = event.value) }
            is JugadorEditEvent.OnDocCompletaChanged -> _uiState.update { it.copy(docCompleta = event.value) }
            is JugadorEditEvent.OnFotoChanged -> _uiState.update { it.copy(fotoUri = event.uri) }

            is JugadorEditEvent.OnGuardarClicked -> onGuardar()
            is JugadorEditEvent.OnEliminarClicked -> onEliminar()
        }
    }

    private fun cargarCategorias() {
        viewModelScope.launch {
            obtenerCategoriasConConteoUseCase().collectLatest { lista ->
                _uiState.update { it.copy(categorias = lista) }
            }
        }
    }

    fun cargarJugador(id: Long) {
        if (id == 0L) {
            _uiState.update {
                JugadorEditState(
                    isNew = true,
                    jugadorId = 0L,
                    isSaved = false,
                    isDeleted = false,
                    isLoading = false,
                    errorMessage = null
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, isSaved = false, isDeleted = false, errorMessage = null) }
            obtenerJugadorPorIdUseCase(id)
                .catch { e -> _uiState.update { it.copy(isLoading = false, errorMessage = e.message) } }
                .collect { jugador ->
                    if (jugador != null) {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                isNew = false,
                                jugadorId = jugador.jugadorId,
                                nombre = jugador.nombre,
                                telefono = jugador.telefono,
                                edad = jugador.edad.toString(),
                                domicilio = jugador.domicilio,
                                categoriaId = if (jugador.categoriaId == 0L) null else jugador.categoriaId,
                                tallaCamiseta = jugador.tallaCamiseta,
                                numeroCamiseta = jugador.numeroCamiseta.toString(),
                                estatura = jugador.estatura.toString(),
                                peso = jugador.peso.toString(),
                                tutorNombre = jugador.tutorNombre,
                                tutorTelefono = jugador.tutorTelefono,
                                tutorVinculo = jugador.tutorVinculo,
                                tutorCorreo = jugador.tutorCorreo,
                                estado = jugador.estado,
                                docCompleta = jugador.docCompleta,
                                fotoUri = jugador.fotoUri,
                                categoriaNombre = jugador.categoriaNombre,

                                tipoInscripcion = jugador.tipoInscripcion,
                                fechaInicio = jugador.fechaInicio,
                                fechaVencimiento = jugador.fechaVencimiento,
                                cuota = jugador.cuota,
                                totalGenerado = jugador.totalGenerado,
                                totalPagado = jugador.totalPagado,
                                deudaActual = jugador.deudaActual
                            )
                        }
                    }
                }
        }
    }

    private fun onGuardar() {
        val currentState = _uiState.value

        val nombreVal = validarNombreJugador(currentState.nombre)
        val telefonoVal = validarTelefonoJugador(currentState.telefono)
        val edadVal = validarEdadJugador(currentState.edad)
        val domicilioVal = validarDomicilioJugador(currentState.domicilio)
        val tallaVal = validarTallaCamisetaJugador(currentState.tallaCamiseta)
        val numeroVal = validarNumeroCamisetaJugador(currentState.numeroCamiseta)
        val estaturaVal = validarEstaturaJugador(currentState.estatura)
        val pesoVal = validarPesoJugador(currentState.peso)
        val tutorNombreVal = validarTutorNombreJugador(currentState.tutorNombre)
        val tutorTelefonoVal = validarTutorTelefonoJugador(currentState.tutorTelefono)
        val tutorVinculoVal = validarTutorVinculoJugador(currentState.tutorVinculo)
        val tutorCorreoVal = validarTutorCorreoJugador(currentState.tutorCorreo)

        if (!nombreVal.isValid || !telefonoVal.isValid || !edadVal.isValid ||
            !domicilioVal.isValid || !tallaVal.isValid || !numeroVal.isValid ||
            !estaturaVal.isValid || !pesoVal.isValid || !tutorNombreVal.isValid ||
            !tutorTelefonoVal.isValid || !tutorVinculoVal.isValid || !tutorCorreoVal.isValid
        ) {
            _uiState.update {
                it.copy(
                    nombreError = nombreVal.error,
                    telefonoError = telefonoVal.error,
                    edadError = edadVal.error,
                    domicilioError = domicilioVal.error,
                    tallaCamisetaError = tallaVal.error,
                    numeroCamisetaError = numeroVal.error,
                    estaturaError = estaturaVal.error,
                    pesoError = pesoVal.error,
                    tutorNombreError = tutorNombreVal.error,
                    tutorTelefonoError = tutorTelefonoVal.error,
                    tutorVinculoError = tutorVinculoVal.error,
                    tutorCorreoError = tutorCorreoVal.error
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val jugador = Jugador(
                jugadorId = currentState.jugadorId,
                nombre = currentState.nombre,
                telefono = currentState.telefono,
                edad = currentState.edad.toIntOrNull() ?: 0,
                domicilio = currentState.domicilio,
                categoriaId = currentState.categoriaId,
                tallaCamiseta = currentState.tallaCamiseta,
                numeroCamiseta = currentState.numeroCamiseta.toIntOrNull() ?: 0,
                estatura = currentState.estatura.toDoubleOrNull() ?: 0.0,
                peso = currentState.peso.toDoubleOrNull() ?: 0.0,
                tutorNombre = currentState.tutorNombre,
                tutorTelefono = currentState.tutorTelefono,
                tutorVinculo = currentState.tutorVinculo,
                tutorCorreo = currentState.tutorCorreo,
                estado = currentState.estado,
                docCompleta = currentState.docCompleta,
                fotoUri = currentState.fotoUri,
                categoriaNombre = currentState.categoriaNombre,

                tipoInscripcion = currentState.tipoInscripcion,
                fechaInicio = currentState.fechaInicio,
                fechaVencimiento = currentState.fechaVencimiento,
                cuota = currentState.cuota,
                totalGenerado = currentState.totalGenerado,
                totalPagado = currentState.totalPagado,
                deudaActual = currentState.deudaActual
            )

            val result = guardarJugadorUseCase(jugador)
            result.onSuccess {
                _uiState.update { it.copy(isSaving = false, isSaved = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(isSaving = false, errorMessage = e.message) }
            }
        }
    }

    private fun onEliminar() {
        val id = _uiState.value.jugadorId
        if (id == 0L) return

        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true) }
            eliminarJugadorUseCase(id)
            _uiState.update { it.copy(isDeleting = false, isDeleted = true) }
        }
    }
}