package com.example.mona.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mona.entity.Oeuvre

@Dao
interface OeuvreDAO {
    @Query("SELECT * FROM artwork_table")
    fun getAll(): LiveData<List<Oeuvre>>

    //In case of booting up the application a second time
    //We dont conflict the primary key by adding twice the same data
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(oeuvres: List<Oeuvre>?)

    @Query("DELETE FROM artwork_table")
    suspend fun deleteAll()
}