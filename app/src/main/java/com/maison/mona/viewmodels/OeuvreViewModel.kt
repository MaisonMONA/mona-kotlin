package com.maison.mona.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.maison.mona.data.OeuvreDatabase
import com.maison.mona.data.OeuvreRepository
import com.maison.mona.entity.Oeuvre
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Class extends AndroidViewModel and requires application as a parameter.
// You can also use a ViewModel to share data between fragments.
// https://developer.android.com/topic/libraries/architecture/viewmodel
class OeuvreViewModel(application: Application) : AndroidViewModel(application) {

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: OeuvreRepository

    // LiveData gives us updated words when they change.
    val oeuvreList: LiveData<List<Oeuvre>>
    //List of Oeuvres
    val oeuvreTList: LiveData<List<Oeuvre>>
    //List of Lieu
    val lieuList: LiveData<List<Oeuvre>>

    val collectedList: LiveData<List<Oeuvre>>

    init {
        // Gets reference to OeuvreDao from OeuvreDatabase to construct
        // the correct OeuvreRepository.
        val database = OeuvreDatabase
        val oeuvreDao = database.getDatabase(
            application,
            viewModelScope
        ).oeuvreDAO()
        repository = OeuvreRepository(oeuvreDao)
        oeuvreList = repository.oeuvreList
        oeuvreTList = repository.getType("artwork")
        lieuList = repository.getType("place")

        collectedList = repository.getAllCollected(2)
    }

    fun updateRating(id: Int, rating: Float?, comment: String?, state: Int?, date: String?){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateRating(id, rating, comment, state, date)
        }
    }

    fun updatePath(id: Int, path: String?){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updatePath(id, path)
        }
    }

//    private fun isNetworkConnected(): Boolean {
//        val cm = getApplication<Application>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val activeNetwork: NetworkInfo? = cm.activeNetworkInfo
//        val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting == true
//        return isConnected
//    }
}

//to bind view models in different fragments visit
//fragment ktx at https://developer.android.com/kotlin/ktx#fragment
//share data between fragments at https://developer.android.com/topic/libraries/architecture/viewmodel.html