package com.example.mona.data

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

    @Query("SELECT * FROM artwork_table WHERE id= :oeuvreId")
    fun getOeuvre(oeuvreId: Int) : Oeuvre

    @Query("SELECT * FROM artwork_table WHERE type= :typeS")
    fun getType(typeS: String) : LiveData<List<Oeuvre>>

    @Query("SELECT * FROM artwork_table WHERE artwork_table.type = :typeS AND artwork_table.state IS NULL")
    fun getNotCollected(typeS: String) : LiveData<List<Oeuvre>>

    //In case of booting up the application a second time
    //We dont conflict the primary key by adding twice the same data
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(oeuvres: List<Oeuvre>?)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(oeuvre: Oeuvre?)

    @Query("DELETE FROM artwork_table")
    suspend fun deleteAll()

    @Query("UPDATE artwork_table SET state= :state, comment=:comment, rating=:rating, date_photo=:date WHERE id = :id")
    fun updateRating(id: Int, rating: Float?, comment: String?, state: Int?, date: String?)

    @Query("UPDATE artwork_table SET photo_path= :path WHERE id = :id")
    fun updatePath(id: Int, path: String?)

    @Query("UPDATE artwork_table SET state= :target WHERE id = :id")
    fun updateTarget(id: Int, target: Int?)

}