package es.uniovi.eii.practicakotlin

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


interface FotoDao {

    @Query("SELECT * FROM fotos")
    fun getAll(): List<Foto?>?


    @Query("SELECT * FROM fotos WHERE url = (:fotoUrl)")
    fun findById(fotoUrl: String): Foto?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun add(foto: Foto?)
}