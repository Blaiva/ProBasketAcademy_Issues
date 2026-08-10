package com.probasketacademy.domain.usecase.jugadores

import com.probasketacademy.domain.usecase.ValidationResult

fun validarNombreJugador(nombre: String): ValidationResult {
    if (nombre.isBlank()) {
        return ValidationResult(isValid = false, error = "El nombre es obligatorio")
    }
    return ValidationResult(isValid = true)
}

fun validarTelefonoJugador(telefono: String): ValidationResult {
    if (telefono.isBlank()) {
        return ValidationResult(isValid = false, error = "El teléfono es obligatorio")
    }
    return ValidationResult(isValid = true)
}

fun validarEdadJugador(edadText: String): ValidationResult {
    val edadInt = edadText.toIntOrNull()
    if (edadText.isBlank() || edadInt == null || edadInt <= 0) {
        return ValidationResult(isValid = false, error = "Ingrese una edad válida")
    }
    return ValidationResult(isValid = true)
}

fun validarDomicilioJugador(domicilio: String): ValidationResult {
    if (domicilio.isBlank()) {
        return ValidationResult(isValid = false, error = "El domicilio es obligatorio")
    }
    return ValidationResult(isValid = true)
}

fun validarTallaCamisetaJugador(talla: String): ValidationResult {
    if (talla.isBlank()) {
        return ValidationResult(isValid = false, error = "La talla de camiseta es obligatoria")
    }
    return ValidationResult(isValid = true)
}

fun validarNumeroCamisetaJugador(numeroText: String): ValidationResult {
    val numeroInt = numeroText.toIntOrNull()
    if (numeroText.isBlank() || numeroInt == null || numeroInt <= 0) {
        return ValidationResult(isValid = false, error = "Ingrese un número de camiseta válido")
    }
    return ValidationResult(isValid = true)
}

fun validarEstaturaJugador(estaturaText: String): ValidationResult {
    val estaturaDouble = estaturaText.toDoubleOrNull()
    if (estaturaText.isBlank() || estaturaDouble == null || estaturaDouble <= 0.0) {
        return ValidationResult(isValid = false, error = "Ingrese una estatura válida en metros")
    }
    return ValidationResult(isValid = true)
}

fun validarPesoJugador(pesoText: String): ValidationResult {
    val pesoDouble = pesoText.toDoubleOrNull()
    if (pesoText.isBlank() || pesoDouble == null || pesoDouble <= 0.0) {
        return ValidationResult(isValid = false, error = "Ingrese un peso válido en kg")
    }
    return ValidationResult(isValid = true)
}

fun validarTutorNombreJugador(tutorNombre: String): ValidationResult {
    if (tutorNombre.isBlank()) {
        return ValidationResult(isValid = false, error = "El nombre del tutor es obligatorio")
    }
    return ValidationResult(isValid = true)
}

fun validarTutorTelefonoJugador(tutorTelefono: String): ValidationResult {
    if (tutorTelefono.isBlank()) {
        return ValidationResult(isValid = false, error = "El teléfono del tutor es obligatorio")
    }
    return ValidationResult(isValid = true)
}

fun validarTutorVinculoJugador(tutorVinculo: String): ValidationResult {
    if (tutorVinculo.isBlank()) {
        return ValidationResult(isValid = false, error = "El vínculo con el tutor es obligatorio")
    }
    return ValidationResult(isValid = true)
}

fun validarTutorCorreoJugador(tutorCorreo: String): ValidationResult {
    if (tutorCorreo.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(tutorCorreo).matches()) {
        return ValidationResult(isValid = false, error = "Ingrese un correo de tutor válido")
    }
    return ValidationResult(isValid = true)
}

fun validarEstadoJugador(estado: String): ValidationResult {
    if (estado.isBlank()) {
        return ValidationResult(isValid = false, error = "El estado es obligatorio")
    }
    return ValidationResult(isValid = true)
}