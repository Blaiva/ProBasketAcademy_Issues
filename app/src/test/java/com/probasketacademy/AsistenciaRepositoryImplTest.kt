package com.probasketacademy

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.probasketacademy.data.local.asistencia.AsistenciaDao
import com.probasketacademy.data.local.asistencia.AsistenciaEntity
import com.probasketacademy.data.local.asistencia.AsistenciaJugadorDto
import com.probasketacademy.data.repository.AsistenciaRepositoryImpl
import com.probasketacademy.domain.model.Asistencia
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
class AsistenciaRepositoryImplTest {

    private lateinit var dao: AsistenciaDao
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var repository: AsistenciaRepositoryImpl

    private val userId = "user-xyz"

    @Before
    fun setUp() {
        dao = mockk(relaxed = true)
        auth = mockk()
        user = mockk()
        every { user.uid } returns userId
        every { auth.currentUser } returns user

        repository = AsistenciaRepositoryImpl(dao, auth)
    }

    @Test
    fun `obtenerListaAsistenciaPorCategoria mapea el dto a dominio con categoria y fecha`() = runTest {
        val dto = AsistenciaJugadorDto(jugadorId = 1, nombreJugador = "Pedro", fotoUri = null, asistio = true)
        every { dao.obtenerListaAsistenciaPorCategoria(7, 1000L, userId) } returns flowOf(listOf(dto))

        val result = repository.obtenerListaAsistenciaPorCategoria(7, 1000L).first()

        assertEquals(1, result.size)
        assertEquals(7L, result[0].categoriaId)
        assertEquals(1000L, result[0].fechaEpocaMs)
        assertEquals(true, result[0].asistio)
    }

    @Test
    fun `registrarAsistencias asigna el userId actual a cada entidad`() = runTest {
        val asistencias = listOf(
            Asistencia(jugadorId = 1, categoriaId = 2, fechaEpocaMs = 500L, asistio = true),
            Asistencia(jugadorId = 3, categoriaId = 2, fechaEpocaMs = 500L, asistio = false)
        )
        val listSlot = slot<List<AsistenciaEntity>>()
        coEvery { dao.registrarAsistencias(capture(listSlot)) } just Runs

        val ids = repository.registrarAsistencias(asistencias)

        assertEquals(2, ids.size)
        assertEquals(true, listSlot.captured.all { it.userId == userId })
    }

    @Test
    fun `obtenerAsistenciasPorDia filtra por userId`() = runTest {
        val entity = AsistenciaEntity(id = 1, jugadorId = 1, categoriaId = 2, fechaEpocaMs = 500L, asistio = true, userId = userId)
        every { dao.obtenerAsistenciasPorDia(500L, userId) } returns flowOf(listOf(entity))

        val result = repository.obtenerAsistenciasPorDia(500L).first()

        assertEquals(1, result.size)
        assertEquals(1L, result[0].jugadorId)
    }

    @Test
    fun `obtenerAsistenciaPromedioPorMes delega en el dao con el userId actual`() = runTest {
        every { dao.obtenerAsistenciaPromedioPorMes(0L, 1000L, userId) } returns flowOf(85.0)

        val result = repository.obtenerAsistenciaPromedioPorMes(0L, 1000L).first()

        assertEquals(85.0, result)
    }
}