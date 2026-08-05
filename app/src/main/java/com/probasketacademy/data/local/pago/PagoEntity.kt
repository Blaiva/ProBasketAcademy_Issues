package com.probasketacademy.data.local.pago

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.probasketacademy.data.local.jugador.JugadorEntity

@Entity(
    tableName = "pagos",
    foreignKeys = [
        ForeignKey(
            entity = JugadorEntity::class,
            parentColumns = ["jugadorId"],
            childColumns = ["jugadorId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["jugadorId"])]
)
data class PagoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val jugadorId: Long,
    val concepto: String,
    val monto: Double,
    val fecha: String,
    val estado: String
)
