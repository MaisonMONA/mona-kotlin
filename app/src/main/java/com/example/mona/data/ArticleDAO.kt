package com.example.mona.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.mona.entity.Article

@Dao
interface ArticleDAO {
    //@Query("SELECT * FROM artwork_table UNION ALL SELECT id,title,NULL,category,NULL,NULL,NULL,NULL,NULL,borough,location,NULL,NULL,state,rating,comment,photo_path,date_photo FROM place_table")
    @Query("SELECT * FROM artwork_table")
    fun getAll(): LiveData<List<Article>>

    @Query("SELECT * FROM artwork_table WHERE id= :articleId")
    fun getArticle(articleId: Int) : Article

    //In case of booting up the application a second time
    //We dont conflict the primary key by adding twice the same data
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(articles: List<Article>?)

    @Query("DELETE FROM artwork_table")
    suspend fun deleteAll()

    @Query("UPDATE artwork_table SET state= :state, comment=:comment, rating=:rating, date_photo=:date WHERE id = :id")
    fun updateRating(id: Int, rating: Float?, comment: String?, state: Int?, date: String?)

    @Query("UPDATE artwork_table SET photo_path= :path WHERE id = :id")
    fun updatePath(id: Int, path: String?)

    @Query("UPDATE artwork_table SET state= :target WHERE id = :id")
    fun updateTarget(id: Int, target: Int?)
}