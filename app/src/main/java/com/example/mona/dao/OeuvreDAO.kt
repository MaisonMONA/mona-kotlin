package com.example.mona.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.mona.entity.Oeuvre

@Dao
interface OeuvreDAO {
    @Query("SELECT * FROM oeuvre")
    fun getAll(): List<Oeuvre>

    @Insert
    fun insertAll(oeuvres: List<Oeuvre>)

}