package com.probasketacademy

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.probasketacademy.data.local.evento.EventoDao
import com.probasketacademy.data.local.evento.EventoEntity
import com.probasketacademy.data.repository.EventoRepositoryImpl
import com.probasketacademy.domain.model.Evento
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
class EventoRepositoryImplTest {

    private lateinit var dao: EventoDao
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var repository: EventoRepositoryImpl

    private val userId = "user-555"

    @Before
    fun setUp() {
        dao = mockk(relaxed = true)
        auth = mockk()
        user = mockk()
        every { user.uid } returns userId
        every { auth.currentUser } returns user

        repository = EventoRepositoryImpl(dao, auth)
    }

    @Test
    fun `guardarEvento asigna el userId actual a la entidad`() = runTest {
        val evento = Evento(titulo = "Partido Amistoso", tipo = "Partido", fechaHoraEpocaMs = 1000L, duracionHoras = 1.5f, lugar = "Cancha 1")
        val entitySlot = slot<EventoEntity>()
        coEvery { dao.guardarEvento(capture(entitySlot)) } just Runs

        repository.guardarEvento(evento)

        assertEquals(userId, entitySlot.captured.userId)
        assertEquals("Partido Amistoso", entitySlot.captured.titulo)
    }

    @Test
    fun `obtenerEventosPorDia filtra por rango y usuario actual`() = runTest {
        val entity = EventoEntity(id = 1, titulo = "Entrenamiento", tipo = "Entrenamiento", fechaHoraEpocaMs = 500L, duracionHoras = 1f, lugar = "Cancha 2", userId = userId)
        every { dao.obtenerEventosPorDia(0L, 1000L, userId) } returns flowOf(listOf(entity))

        val result = repository.obtenerEventosPorDia(0L, 1000L).first()

        assertEquals(1, result.size)
        assertEquals("Entrenamiento", result[0].titulo)
    }

    @Test
    fun `eliminarEvento delega en el dao con el userId actual`() = runTest {
        coEvery { dao.eliminarEvento(9, userId) } just Runs

        repository.eliminarEvento(9)

        coVerify { dao.eliminarEvento(9, userId) }
    }
}