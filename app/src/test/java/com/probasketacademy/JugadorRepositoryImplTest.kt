package com.probasketacademy

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.probasketacademy.data.local.jugador.JugadorConCategoriaDto
import com.probasketacademy.data.local.jugador.JugadorDao
import com.probasketacademy.data.local.jugador.JugadorEntity
import com.probasketacademy.data.repository.JugadorRepositoryImpl
import com.probasketacademy.data.repository.UserSessionProvider
import com.probasketacademy.domain.model.Jugador
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
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
class JugadorRepositoryImplTest {

    private lateinit var dao: JugadorDao
    private lateinit var repository: JugadorRepositoryImpl

    private lateinit var userSession: UserSessionProvider
    private val userId = "user-123"

    @Before
    fun setUp() {
        dao = mockk(relaxed = true)
        userSession = mockk()
        every { userSession.currentUserId } returns userId
        every { userSession.observeUserId() } returns flowOf(userId)

        repository = JugadorRepositoryImpl(dao, userSession)
    }

    @Test
    fun `guardarJugador asigna el userId del usuario actual`() = runTest {
        val jugador = Jugador(jugadorId = 0, nombre = "Juan Perez", numeroCamiseta = 10)
        val entitySlot = slot<JugadorEntity>()
        coEvery { dao.guardarJugador(capture(entitySlot)) } just Runs

        repository.guardarJugador(jugador)

        coVerify { dao.guardarJugador(any()) }
        assertEquals(userId, entitySlot.captured.userId)
        assertEquals("Juan Perez", entitySlot.captured.nombre)
    }

    @Test
    fun `obtenerJugadorPorId filtra por id y userId`() = runTest {
        val dto = jugadorDto(jugadorId = 5)
        every { dao.obtenerJugadorConCategoriaPorId(5, userId) } returns flowOf(dto)

        val result = repository.obtenerJugadorPorId(5).first()

        assertEquals(5L, result?.jugadorId)
        assertEquals("Sin Categoría", result?.categoriaNombre)
    }

    @Test
    fun `obtenerJugadores retorna solo los jugadores del usuario actual`() = runTest {
        val lista = listOf(jugadorDto(1), jugadorDto(2))
        every { dao.obtenerJugadoresConCategoria(userId) } returns flowOf(lista)

        val result = repository.obtenerJugadores().first()

        assertEquals(2, result.size)
    }

    @Test
    fun `obtenerJugadoresPorCategoria delega en el dao con el userId actual`() = runTest {
        val lista = listOf(jugadorDto(1, categoriaId = 9))
        every { dao.obtenerJugadoresPorCategoria(9, userId) } returns flowOf(lista)

        val result = repository.obtenerJugadoresPorCategoria(9).first()

        assertEquals(1, result.size)
        assertEquals(9L, result[0].categoriaId)
    }

    @Test
    fun `buscarJugadores delega la busqueda en el dao`() = runTest {
        every { dao.buscarJugadoresConCategoria("juan", userId) } returns flowOf(listOf(jugadorDto(1, nombre = "Juan")))

        val result = repository.buscarJugadores("juan").first()

        assertEquals(1, result.size)
        assertEquals("Juan", result[0].nombre)
    }

    @Test
    fun `eliminarJugador elimina solo si pertenece al usuario actual`() = runTest {
        coEvery { dao.eliminarJugadorPorId(3, userId) } just Runs

        repository.eliminarJugador(3)

        coVerify { dao.eliminarJugadorPorId(3, userId) }
    }

    private fun jugadorDto(
        jugadorId: Long,
        nombre: String = "Jugador $jugadorId",
        categoriaId: Long? = null
    ) = JugadorConCategoriaDto(
        jugadorId = jugadorId,
        nombre = nombre,
        telefono = "809-000-0000",
        edad = 15,
        domicilio = "Calle Falsa 123",
        categoriaId = categoriaId,
        tallaCamiseta = "M",
        numeroCamiseta = 7,
        estatura = 1.6,
        peso = 55.0,
        tutorNombre = "Tutor",
        tutorTelefono = "809-111-1111",
        tutorVinculo = "Padre",
        tutorCorreo = "tutor@correo.com",
        estado = "Activo",
        docCompleta = true,
        fotoUri = null,
        actaNacimientoUri = null,
        categoriaNombre = null,
        tipoInscripcion = "Mensual",
        fechaInicio = "",
        fechaVencimiento = "",
        cuota = 0.0,
        totalGenerado = 0.0,
        totalPagado = 0.0,
        deudaActual = 0.0
    )
}