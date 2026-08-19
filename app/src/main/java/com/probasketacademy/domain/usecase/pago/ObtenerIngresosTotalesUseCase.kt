package com.probasketacademy.domain.usecase.pago

import com.probasketacademy.domain.repository.PagoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObtenerIngresosTotalesUseCase @Inject constructor(
    private val repository: PagoRepository
) {
    operator fun invoke(): Flow<Double?> {
        return repository.obtenerIngresosTotales()
    }
}