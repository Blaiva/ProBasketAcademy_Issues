package com.probasketacademy.domain.usecase.evento

import com.probasketacademy.domain.model.Evento
import com.probasketacademy.domain.repository.EventoRepository
import javax.inject.Inject

class GuardarEventoUseCase @Inject constructor(
    private val repository: EventoRepository
) {
    suspend operator fun invoke(evento: Evento): Result<Long> {
        val tituloVal = validarTituloEvento(evento.titulo)
        val tipoVal = validarTipoEvento(evento.tipo)
        val duracionVal = validarDuracionEvento(evento.duracionHoras)

        if (!tituloVal.isValid || !tipoVal.isValid || !duracionVal.isValid) {
            val error = listOfNotNull(tituloVal.error, tipoVal.error, duracionVal.error).firstOrNull()
                ?: "Datos del evento inválidos"
            return Result.failure(IllegalArgumentException(error))
        }

        return runCatching { repository.guardarEvento(evento) }
    }
}