package com.probasketacademy.domain.model

data class Jugador(
    val jugadorId: Long = 0,
    val nombre: String = "",
    val telefono: String = "",
    val edad: Int = 0,
    val domicilio: String = "",
    val categoriaId: Long? = null,
    val tallaCamiseta: String = "",
    val numeroCamiseta: Int = 0,
    val estatura: Double = 0.0,
    val peso: Double = 0.0,
    val tutorNombre: String = "",
    val tutorTelefono: String = "",
    val tutorVinculo: String = "",
    val tutorCorreo: String = "",
    val estado: String = "Activo",
    val docCompleta: Boolean = true,
    val fotoUri: String? = null,
    val categoriaNombre: String = "",

    // --- Campos Ocultos para Finanzas ---
    val tipoInscripcion: String = "Mensual",
    val fechaInicio: String = "",
    val fechaVencimiento: String = "",
    val cuota: Double = 0.0,
    val totalGenerado: Double = 0.0,
    val totalPagado: Double = 0.0,
    val deudaActual: Double = 0.0
)