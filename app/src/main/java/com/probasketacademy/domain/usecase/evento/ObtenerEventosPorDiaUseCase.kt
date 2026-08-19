package com.probasketacademy.domain.usecase.evento

import com.probasketacademy.domain.model.Evento
import com.probasketacademy.domain.repository.EventoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObtenerEventosPorDiaUseCase @Inject constructor(
    private val repository: EventoRepository
) {
    operator fun invoke(inicioDia: Long, finDia: Long): Flow<List<Evento>> {
        return repository.obtenerEventosPorDia(inicioDia, finDia)
    }
}