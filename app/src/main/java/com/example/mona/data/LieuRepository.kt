package com.example.mona.data

import androidx.lifecycle.LiveData
import com.example.mona.entity.Lieu

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
//the Repository implements the logic for deciding whether to fetch data from a network or use results cached in a local database
class LieuRepository(private val lieuDAO: LieuDAO) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val lieuList: LiveData<List<Lieu>> = lieuDAO.getAll()

    suspend fun insertAll(lieuList: List<Lieu>){
        lieuDAO.insertAll(lieuList)
    }

}