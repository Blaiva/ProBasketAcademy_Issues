package com.probasketacademy.domain.usecase.categoria

import com.probasketacademy.domain.usecase.ValidationResult

fun validarNombreCategoria(nombre: String, categoriasExistentes: List<String>): ValidationResult{
    return when{
        nombre.isBlank() -> ValidationResult(false, "El nombre de la categoria es requerido")
        nombre.trim().length < 3 -> ValidationResult(false, "Minimo 3 caracteres")
        categoriasExistentes.any {it.equals(nombre.trim(), ignoreCase = true)} -> ValidationResult(false, "Esta categoria ya esta registrada")
        else -> ValidationResult(true)
    }
}