package com.example.mona.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.mona.data.OeuvreDatabase
import com.example.mona.data.OeuvreRepository
import com.example.mona.entity.Oeuvre
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

    init {
        // Gets reference to OeuvreDao from OeuvreDatabase to construct
        // the correct OeuvreRepository.
        val oeuvreDao = OeuvreDatabase.getDatabase(
            application,
            viewModelScope
        ).oeuvreDAO()
        repository = OeuvreRepository(oeuvreDao)
        oeuvreList = repository.oeuvreList
        oeuvreTList = repository.getType("artwork")
        lieuList = repository.getType("place")
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



}

//to bind view models in different fragments visit
//fragment ktx at https://developer.android.com/kotlin/ktx#fragment
//share data between fragments at https://developer.android.com/topic/libraries/architecture/viewmodel.html