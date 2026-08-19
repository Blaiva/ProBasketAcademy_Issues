package com.probasketacademy.domain.usecase.evento

import com.probasketacademy.domain.usecase.ValidationResult

fun validarTituloEvento(titulo: String): ValidationResult {
    if (titulo.isBlank()) {
        return ValidationResult(isValid = false, error = "El concepto del evento es obligatorio")
    }
    return ValidationResult(isValid = true)
}

fun validarTipoEvento(tipo: String): ValidationResult {
    if (tipo.isBlank()) {
        return ValidationResult(isValid = false, error = "El tipo de evento es obligatorio")
    }
    return ValidationResult(isValid = true)
}

fun validarDuracionEvento(duracionHoras: Float): ValidationResult {
    if (duracionHoras <= 0f) {
        return ValidationResult(isValid = false, error = "La duración debe ser mayor a 0")
    }
    return ValidationResult(isValid = true)
}