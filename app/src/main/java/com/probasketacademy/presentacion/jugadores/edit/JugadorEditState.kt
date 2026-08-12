package com.probasketacademy.presentacion.jugadores.edit

import com.probasketacademy.data.local.categoria.CategoriaConConteoDto
import com.probasketacademy.domain.model.Categoria
import com.probasketacademy.domain.model.Jugador

data class JugadorEditState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val isSaved: Boolean = false,
    val isDeleted: Boolean = false,
    val isNew: Boolean = true,
    val errorMessage: String? = null,

    // Campos del formulario
    val jugadorId: Long = 0,
    val nombre: String = "",
    val telefono: String = "",
    val edad: String = "",
    val domicilio: String = "",
    val categoriaId: Long? = null,
    val tallaCamiseta: String = "",
    val numeroCamiseta: String = "",
    val estatura: String = "",
    val peso: String = "",
    val tutorNombre: String = "",
    val tutorTelefono: String = "",
    val tutorVinculo: String = "",
    val tutorCorreo: String = "",
    val estado: String = "Activo",
    val docCompleta: Boolean = true,
    val fotoUri: String? = null,
    val categoriaNombre: String = "",

    // Listas para Dropdowns
    val categorias: List<Categoria> = emptyList(),

    // Mensajes de error por campo
    val nombreError: String? = null,
    val telefonoError: String? = null,
    val edadError: String? = null,
    val domicilioError: String? = null,
    val tallaCamisetaError: String? = null,
    val numeroCamisetaError: String? = null,
    val estaturaError: String? = null,
    val pesoError: String? = null,
    val tutorNombreError: String? = null,
    val tutorTelefonoError: String? = null,
    val tutorVinculoError: String? = null,
    val tutorCorreoError: String? = null
)