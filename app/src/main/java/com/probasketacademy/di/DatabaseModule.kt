package com.probasketacademy.di

import android.content.Context
import androidx.room.Room
import com.probasketacademy.data.database.AppDatabase
import com.probasketacademy.data.local.asistencia.AsistenciaDao
import com.probasketacademy.data.local.categoria.CategoriaDao
import com.probasketacademy.data.local.evento.EventoDao
import com.probasketacademy.data.local.jugador.JugadorDao
import com.probasketacademy.data.local.pago.PagoDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase{
        return Room.databaseBuilder(
            context, AppDatabase::class.java, "probasket_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideJugadorDao(database: AppDatabase): JugadorDao {
        return database.jugadorDao()
    }

    @Provides
    @Singleton
    fun provideCategoriaDao(database: AppDatabase): CategoriaDao {
        return database.categoriaDao()
    }

    @Provides
    @Singleton
    fun provideAsistenciaDao(database: AppDatabase): AsistenciaDao {
        return database.asistenciaDao()
    }

    @Provides
    @Singleton
    fun providePagoDao(database: AppDatabase): PagoDao {
        return database.pagoDao()
    }

    @Provides
    @Singleton
    fun provideEventoDao(database: AppDatabase): EventoDao {
        return database.eventoDao()
    }
}