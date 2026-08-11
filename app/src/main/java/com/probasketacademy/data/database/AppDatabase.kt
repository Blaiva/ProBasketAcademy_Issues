package com.probasketacademy.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.probasketacademy.data.local.asistencia.AsistenciaDao
import com.probasketacademy.data.local.asistencia.AsistenciaEntity
import com.probasketacademy.data.local.categoria.CategoriaDao
import com.probasketacademy.data.local.categoria.CategoriaEntity
import com.probasketacademy.data.local.evento.EventoDao
import com.probasketacademy.data.local.evento.EventoEntity
import com.probasketacademy.data.local.jugador.JugadorDao
import com.probasketacademy.data.local.jugador.JugadorEntity
import com.probasketacademy.data.local.pago.PagoDao
import com.probasketacademy.data.local.pago.PagoEntity

@Database(
    entities = [
        CategoriaEntity::class,
        JugadorEntity::class,
        AsistenciaEntity::class,
        PagoEntity::class,
        EventoEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase: RoomDatabase() {
    abstract fun categoriaDao(): CategoriaDao
    abstract fun jugadorDao(): JugadorDao
    abstract fun asistenciaDao(): AsistenciaDao
    abstract fun pagoDao(): PagoDao
    abstract fun eventoDao(): EventoDao
}