package com.probasketacademy.domain.usecase.evento

import com.probasketacademy.domain.repository.EventoRepository
import javax.inject.Inject

class EliminarEventoUseCase @Inject constructor(
    private val repository: EventoRepository
) {
    suspend operator fun invoke(id: Long) {
        repository.eliminarEvento(id)
    }
}