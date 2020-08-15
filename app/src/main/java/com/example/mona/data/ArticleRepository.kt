package com.example.mona.data

import androidx.lifecycle.LiveData
import com.example.mona.entity.Article

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
//the Repository implements the logic for deciding whether to fetch data from a network or use results cached in a local database
class ArticleRepository(private val articleDAO: ArticleDAO) {

    val articleList: LiveData<List<Article>> = articleDAO.getAll()

    fun getArticle(ArticleId: Int) = articleDAO.getArticle(ArticleId)

    suspend fun updateRating(id: Int, rating: Float?, comment: String?, state: Int?, date: String?){
        articleDAO.updateRating(id, rating, comment, state, date)
    }

    suspend fun updatePath(id: Int, path: String?){
        articleDAO.updatePath(id, path)
    }

    suspend fun updateTarget(oeuvreId: Int, target: Int?){
        articleDAO.updateTarget(oeuvreId, target)
    }

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: ArticleRepository? = null

        fun getInstance(articleDAO: ArticleDAO) =
            instance ?: synchronized(this) {
                instance ?: ArticleRepository(articleDAO).also { instance = it }
            }
    }

}