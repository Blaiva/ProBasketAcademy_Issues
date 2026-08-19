package com.probasketacademy

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.probasketacademy.data.local.categoria.CategoriaConConteoDto
import com.probasketacademy.data.local.categoria.CategoriaDao
import com.probasketacademy.data.local.categoria.CategoriaEntity
import com.probasketacademy.data.repository.CategoriaRepositoryImpl
import com.probasketacademy.data.repository.UserSessionProvider
import com.probasketacademy.domain.model.Categoria
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
class CategoriaRepositoryImplTest {

    private lateinit var dao: CategoriaDao
    private lateinit var repository: CategoriaRepositoryImpl

    private lateinit var userSession: UserSessionProvider
    private val userId = "user-123"

    @Before
    fun setUp() {
        dao = mockk(relaxed = true)
        userSession = mockk()
        every { userSession.currentUserId } returns userId
        every { userSession.observeUserId() } returns flowOf(userId)

        repository = CategoriaRepositoryImpl(dao, userSession)
    }

    @Test
    fun `guardarCategoria asigna el userId actual a la entidad`() = runTest {
        val categoria = Categoria(id = 0, nombre = "Sub-15")
        val entitySlot = slot<CategoriaEntity>()
        coEvery { dao.guardarCategoria(capture(entitySlot)) } just Runs

        repository.guardarCategoria(categoria)

        assertEquals(userId, entitySlot.captured.userId)
        assertEquals("Sub-15", entitySlot.captured.nombre)
    }

    @Test
    fun `obtenerCategoriasConConteo filtra por el usuario actual`() = runTest {
        val dtos = listOf(CategoriaConConteoDto(1, "Sub-13", 5))
        every { dao.obtenerCategoriasConConteo(userId) } returns flowOf(dtos)

        val result = repository.obtenerCategoriasConConteo().first()

        assertEquals(1, result.size)
        assertEquals("Sub-13", result[0].nombre)
    }

    @Test
    fun `obtenerCategoriaPorId retorna null si no pertenece al usuario`() = runTest {
        every { dao.obtenerCategoriaConConteoPorId(99, userId) } returns flowOf(null)

        val result = repository.obtenerCategoriaPorId(99).first()

        assertEquals(null, result)
    }

    @Test
    fun `eliminarCategoria delega en el dao con el userId actual`() = runTest {
        coEvery { dao.eliminarCategoria(4, userId) } just Runs

        repository.eliminarCategoria(4)

        coVerify { dao.eliminarCategoria(4, userId) }
    }
}