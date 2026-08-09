package com.probasketacademy.domain.usecase

data class ValidationResult(
    val isValid: Boolean,
    val error: String? = null
)
