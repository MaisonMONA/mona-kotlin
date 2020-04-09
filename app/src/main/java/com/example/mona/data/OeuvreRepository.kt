package com.example.mona.data

import androidx.lifecycle.LiveData
import com.example.mona.entity.Oeuvre

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
//the Repository implements the logic for deciding whether to fetch data from a network or use results cached in a local database
class OeuvreRepository(private val oeuvreDao: OeuvreDAO) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val oeuvreList: LiveData<List<Oeuvre>> = oeuvreDao.getAll()

    fun getOeuvre(oeuvreId: Int) = oeuvreDao.getOeuvre(oeuvreId)

    suspend fun updateRating(id: Int, rating: Float?, comment: String?, state: Int?, date: String?){
        oeuvreDao.updateRating(id, rating, comment, state, date)
    }

    suspend fun updatePath(id: Int, path: String?){
        oeuvreDao.updatePath(id, path)
    }

    suspend fun updateTarget(oeuvreId: Int, target: Int?){
        oeuvreDao.updateTarget(oeuvreId, target)
    }


    companion object {

        // For Singleton instantiation
        @Volatile private var instance: OeuvreRepository? = null

        fun getInstance(oeuvreDao: OeuvreDAO) =
            instance ?: synchronized(this) {
                instance ?: OeuvreRepository(oeuvreDao).also { instance = it }
            }
    }


}