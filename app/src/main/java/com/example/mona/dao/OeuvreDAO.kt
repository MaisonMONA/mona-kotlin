package com.example.mona.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.mona.entity.Oeuvre

@Dao
interface OeuvreDAO {
    @Query("SELECT * FROM artwork_table")
    fun getAll(): LiveData<List<Oeuvre>>

    //In case of booting up the application a second time
    //We dont conflict the primary key by adding twice the same data
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(oeuvres: List<Oeuvre>?)

    @Query("DELETE FROM artwork_table")
    suspend fun deleteAll()

    @Query("UPDATE artwork_table SET state= :state, photo_path=:path, comment=:comment, rating=:rating, date_photo=:date WHERE id = :id")
    fun updateArtwork(id: Int, rating: Float?, comment: String?, state: Int?, path: String?, date: String?)

}