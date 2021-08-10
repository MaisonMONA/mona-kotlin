package com.maison.mona.data

import androidx.lifecycle.LiveData
import com.maison.mona.entity.Oeuvre

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
//the Repository implements the logic for deciding whether to fetch data from a network or use results cached in a local database
class OeuvreRepository(private val oeuvreDao: OeuvreDAO) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    //Get all articles
    val oeuvreList: LiveData<List<Oeuvre>> = oeuvreDao.getAll()
    //Get all of one type
    fun getType(type: String) = oeuvreDao.getType(type)

//    fun getRandomUncollected(type:String){
//        val listUncollected = oeuvreDao.getNotCollexcted("artwork")
//        val randNumber = (0..(listUncollected.value?.size!!))
//    }

    fun getAllCollected(state:Int): LiveData<List<Oeuvre>>{
        return oeuvreDao.getCollected(state)
    }

    fun getArticleById(articleId: Int): Oeuvre {
        return oeuvreDao.getOeuvre(articleId)
    }

    fun updateRating(id: Int, rating: Float?, comment: String?, state: Int?, date: String?){
        oeuvreDao.updateRating(id, rating, comment, state, date)
    }

    fun updatePath(id: Int, path: String?){
        oeuvreDao.updatePath(id, path)
    }

    fun updateTarget(oeuvreId: Int, target: Int?){
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