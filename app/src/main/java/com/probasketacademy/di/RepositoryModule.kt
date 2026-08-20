package com.probasketacademy.di

import com.probasketacademy.data.repository.AsistenciaRepositoryImpl
import com.probasketacademy.data.repository.CategoriaRepositoryImpl
import com.probasketacademy.data.repository.EventoRepositoryImpl
import com.probasketacademy.data.repository.JugadorRepositoryImpl
import com.probasketacademy.data.repository.PagoRepositoryImpl
import com.probasketacademy.domain.repository.AsistenciaRepository
import com.probasketacademy.domain.repository.CategoriaRepository
import com.probasketacademy.domain.repository.EventoRepository
import com.probasketacademy.domain.repository.JugadorRepository
import com.probasketacademy.domain.repository.PagoRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    @Singleton
    fun bindJugadorRepository(
        impl: JugadorRepositoryImpl
    ): JugadorRepository

    @Binds
    @Singleton
    fun bindCategoriaRepository(
        impl: CategoriaRepositoryImpl
    ): CategoriaRepository

    @Binds
    @Singleton
    fun bindAsistenciaRepository(
        impl: AsistenciaRepositoryImpl
    ): AsistenciaRepository

    @Binds
    @Singleton
    fun bindPagoRepository(
        impl: PagoRepositoryImpl
    ): PagoRepository

    @Binds
    @Singleton
    fun bindEventoRepository(
        impl: EventoRepositoryImpl
    ): EventoRepository
}