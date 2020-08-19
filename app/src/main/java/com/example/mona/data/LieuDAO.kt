package com.example.mona.data
/*
import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mona.entity.Lieu

@Dao
interface LieuDAO {
    @Query("SELECT * FROM place_table")
    fun getAll(): LiveData<List<Lieu>>

    @Query("SELECT * FROM place_table WHERE id= :lieuId")
    fun getLieu(lieuId: Int) : Lieu

    //In case of booting up the application a second time
    //We dont conflict the primary key by adding twice the same data
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(places: List<Lieu>?)

    @Query("DELETE FROM place_table")
    suspend fun deleteAll()

    @Query("UPDATE place_table SET state= :state, comment=:comment, rating=:rating, date_photo=:date WHERE id = :id")
    fun updateRating(id: Int, rating: Float?, comment: String?, state: Int?, date: String?)

    @Query("UPDATE place_table SET photo_path= :path WHERE id = :id")
    fun updatePath(id: Int, path: String?)

    @Query("UPDATE place_table SET state= :target WHERE id = :id")
    fun updateTarget(id: Int, target: Int?)


}
 */