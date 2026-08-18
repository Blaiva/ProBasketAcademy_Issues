package com.probasketacademy

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.probasketacademy.data.local.pago.PagoConJugadorDto
import com.probasketacademy.data.local.pago.PagoDao
import com.probasketacademy.data.local.pago.PagoEntity
import com.probasketacademy.data.repository.PagoRepositoryImpl
import com.probasketacademy.domain.model.Pago
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.slot
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PagoRepositoryImplTest {

    private lateinit var dao: PagoDao
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var repository: PagoRepositoryImpl

    private val userId = "user-999"

    @Before
    fun setUp() {
        dao = mockk(relaxed = true)
        auth = mockk()
        user = mockk()
        every { user.uid } returns userId
        every { auth.currentUser } returns user

        repository = PagoRepositoryImpl(dao, auth)
    }

    @Test
    fun `registrarPago asigna el userId actual a la entidad`() = runTest {
        val pago = Pago(jugadorId = 1, concepto = "Cuota", montoTotal = 500.0, montoPagado = 500.0, deuda = 0.0, fecha = "01/01/2026", estado = "PAGADO")
        val entitySlot = slot<PagoEntity>()
        coEvery { dao.registrarPago(capture(entitySlot)) } just Runs

        repository.registrarPago(pago)

        assertEquals(userId, entitySlot.captured.userId)
        assertEquals("Cuota", entitySlot.captured.concepto)
    }

    @Test
    fun `obtenerPagosPorJugador filtra por jugador y usuario actual`() = runTest {
        val dto = pagoDto(pagoId = 1, jugadorId = 4)
        every { dao.obtenerPagosPorJugador(4, userId) } returns flowOf(listOf(dto))

        val result = repository.obtenerPagosPorJugador(4).first()

        assertEquals(1, result.size)
        assertEquals(4L, result[0].jugadorId)
    }

    @Test
    fun `obtenerCobrosPendientes retorna solo los pendientes del usuario actual`() = runTest {
        every { dao.obtenerCobrosPendientes(userId) } returns flowOf(listOf(pagoDto(pagoId = 2, estado = "PENDIENTE")))

        val result = repository.obtenerCobrosPendientes().first()

        assertEquals(1, result.size)
        assertEquals("PENDIENTE", result[0].estado)
    }

    @Test
    fun `obtenerIngresosTotales delega en el dao con el userId actual`() = runTest {
        every { dao.obtenerIngresosTotales(userId) } returns flowOf(1500.0)

        val result = repository.obtenerIngresosTotales().first()

        assertEquals(1500.0, result)
    }

    private fun pagoDto(
        pagoId: Long,
        jugadorId: Long = 1,
        estado: String = "PAGADO"
    ) = PagoConJugadorDto(
        pagoId = pagoId,
        jugadorId = jugadorId,
        concepto = "Cuota Mensual",
        montoTotal = 500.0,
        montoPagado = 500.0,
        deuda = 0.0,
        fecha = "01/01/2026",
        estado = estado,
        jugadorNombre = "Jugador $jugadorId",
        numeroCamiseta = 10
    )
}