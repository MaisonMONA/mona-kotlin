package com.example.mona.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mona.entity.Lieu

@Dao
interface LieuDAO {
    @Query("SELECT * FROM place_table")
    fun getAll(): LiveData<List<Lieu>>

    //In case of booting up the application a second time
    //We dont conflict the primary key by adding twice the same data
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(places: List<Lieu>?)

    @Query("DELETE FROM place_table")
    suspend fun deleteAll()
}