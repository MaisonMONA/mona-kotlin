package com.example.mona

import androidx.lifecycle.LiveData
import com.example.mona.dao.OeuvreDAO
import com.example.mona.entity.Oeuvre

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
//the Repository implements the logic for deciding whether to fetch data from a network or use results cached in a local database
class OeuvreRepository(private val oeuvreDao: OeuvreDAO) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val oeuvreList: LiveData<List<Oeuvre>> = oeuvreDao.getAll()

    suspend fun insertAll(oeuvreList: List<Oeuvre>){
        oeuvreDao.insertAll(oeuvreList)
    }

    suspend fun insert(oeuvre:Oeuvre ){
        oeuvreDao.insert(oeuvre)
    }

    suspend fun updateArtwork(id: Int, rating: Float?, comment: String?, state: Int?, path: String?, date: String?){
        oeuvreDao.updateArtwork(id, rating, comment, state, path, date)
    }


}