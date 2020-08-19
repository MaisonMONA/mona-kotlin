package com.example.mona.data
/*
import androidx.lifecycle.LiveData
import com.example.mona.entity.Lieu

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
//the Repository implements the logic for deciding whether to fetch data from a network or use results cached in a local database
class LieuRepository(private val lieuDAO: LieuDAO) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val lieuList: LiveData<List<Lieu>> = lieuDAO.getAll()
   fun getLieu(lieuId: Int) = lieuDAO.getLieu(lieuId)

    suspend fun updateRating(id: Int, rating: Float?, comment: String?, state: Int?, date: String?){
        lieuDAO.updateRating(id, rating, comment, state, date)
    }

    suspend fun updatePath(id: Int, path: String?){
        lieuDAO.updatePath(id, path)
    }

    suspend fun updateTarget(oeuvreId: Int, target: Int?){
        lieuDAO.updateTarget(oeuvreId, target)
    }

    companion object {

        // For Singleton instantiation
        @Volatile private var instance: LieuRepository? = null

        fun getInstance(lieuDAO: LieuDAO) =
            instance ?: synchronized(this) {
                instance ?: LieuRepository(lieuDAO).also { instance = it }
            }
    }

}
 */