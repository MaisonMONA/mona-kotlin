package com.example.mona

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.mona.entity.Oeuvre
import kotlinx.coroutines.launch

// Class extends AndroidViewModel and requires application as a parameter.
// You can also use a ViewModel to share data between fragments.
// https://developer.android.com/topic/libraries/architecture/viewmodel
class OeuvreViewModel(application: Application) : AndroidViewModel(application) {

    // The ViewModel maintains a reference to the repository to get data.
    private val repository: OeuvreRepository
    // LiveData gives us updated words when they change.
    val oeuvreList: LiveData<List<Oeuvre>>

    init {
        // Gets reference to WordDao from WordRoomDatabase to construct
        // the correct WordRepository.
        val oeuvreDao = AppDatabase.getDatabase(application,viewModelScope).oeuvreDAO()
        repository = OeuvreRepository(oeuvreDao)
        oeuvreList = repository.oeuvreList
    }

    fun insertAll(oeuvreList: List<Oeuvre>) = viewModelScope.launch {
        repository.insertAll(oeuvreList)
    }

    fun updateArtwork(id: Int, rating: Float?, comment: String?, state: Int?, path: String?, date: String?) = viewModelScope.launch {
        repository.updateArtwork(id, rating, comment, state, path, date)
    }



}

//to bind view models in different fragments visit
//fragment ktx at https://developer.android.com/kotlin/ktx#fragment
//share data between fragments at https://developer.android.com/topic/libraries/architecture/viewmodel.html