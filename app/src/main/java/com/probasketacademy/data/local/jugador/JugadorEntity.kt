package com.probasketacademy.data.local.jugador

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.probasketacademy.data.local.categoria.CategoriaEntity

@Entity(
    tableName = "jugadores",
    foreignKeys = [
        ForeignKey(
            entity = CategoriaEntity::class,
            parentColumns = ["id"],
            childColumns = ["categoriaId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["categoriaId"])]
)
data class JugadorEntity(
    @PrimaryKey(autoGenerate = true)
    val jugadorId: Long = 0,
    val nombre: String,
    val telefono: String,
    val edad: Int,
    val domicilio: String,
    val categoriaId: Long?,
    val tallaCamiseta: String,
    val numeroCamiseta: Int = 0,
    val estatura: Double,
    val peso: Double,
    val tutorNombre: String,
    val tutorTelefono: String,
    val tutorVinculo: String,
    val tutorCorreo: String,
    val estado: String = "Activo",
    val docCompleta: Boolean = true,
    val fotoUri: String? = null,
    val actaNacimientoUri: String? = null,
    val tipoInscripcion: String = "Mensual",
    val fechaInicio: String = "",
    val fechaVencimiento: String = "",
    val cuota: Double = 0.0,
    val totalGenerado: Double = 0.0,
    val totalPagado: Double = 0.0,
    val deudaActual: Double = 0.0,
    val userId: String = ""
)