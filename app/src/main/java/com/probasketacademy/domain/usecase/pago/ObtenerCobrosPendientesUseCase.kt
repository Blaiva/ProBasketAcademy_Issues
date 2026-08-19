package com.probasketacademy.domain.usecase.pago

import com.probasketacademy.domain.model.Pago
import com.probasketacademy.domain.repository.PagoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObtenerCobrosPendientesUseCase @Inject constructor(
    private val repository: PagoRepository
) {
    operator fun invoke(): Flow<List<Pago>> {
        return repository.obtenerCobrosPendientes()
    }
}