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
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindJugadorRepository(
        impl: JugadorRepositoryImpl
    ): JugadorRepository

    @Binds
    @Singleton
    abstract fun bindCategoriaRepository(
        impl: CategoriaRepositoryImpl
    ): CategoriaRepository

    @Binds
    @Singleton
    abstract fun bindAsistenciaRepository(
        impl: AsistenciaRepositoryImpl
    ): AsistenciaRepository

    @Binds
    @Singleton
    abstract fun bindPagoRepository(
        impl: PagoRepositoryImpl
    ): PagoRepository

    @Binds
    @Singleton
    abstract fun bindEventoRepository(
        impl: EventoRepositoryImpl
    ): EventoRepository
}