package es.uniovi.eii.practicakotlin

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/*
    Las Data classes simplemente almacenan información.
    Reciben lo necesario en la construcción.
    Generan automáticamente:
    - Código para el acceso a los atributos.
    - toString()
    - equals()
    - hashcode()
    - Y más: https://kotlinlang.org/docs/data-classes.html

 */
@Entity(tableName = "fotos")
data class Foto (
    @PrimaryKey
    @ColumnInfo(name = "url")
    val url : String,
    @ColumnInfo(name = "titulo")
    val titulo : String
)
