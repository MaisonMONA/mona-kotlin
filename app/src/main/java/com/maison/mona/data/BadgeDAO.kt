package com.maison.mona.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.maison.mona.entity.Badge_2

@Dao
interface BadgeDAO {
    @Query("SELECT * FROM badge_table")
    fun getAll(): LiveData<List<Badge_2>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(oeuvres: List<Badge_2>?)

    @Query("UPDATE badge_table SET isCollected= :collected WHERE id= :id")
    fun updateCollected(id: Int, collected: Boolean?)

//    @Query("SELECT * FROM badge_table WHERE badge_table.isCollected = :collected AND badge_table.quartier = :typeS")
//    fun getBadgeByQuartier(typeS: String, collected: Boolean = false) : LiveData<List<Badge_2>>
}